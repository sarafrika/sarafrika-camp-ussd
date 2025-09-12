package apps.sarafrika.service;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.entity.Activity;
import apps.sarafrika.entity.Camp;
import apps.sarafrika.entity.Registration;
import apps.sarafrika.enums.NavigationType;
import apps.sarafrika.util.UssdResponseBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UssdMenuService {

    private static final Logger LOG = Logger.getLogger(UssdMenuService.class);
    private static final int PAGE_SIZE = 3;

    @Inject
    CampService campService;

    @Inject
    RegistrationService registrationService;

    @Inject
    TrackingService trackingService;

    @Inject
    SmsNotificationService smsNotificationService;

    @Inject
    LocationService locationService;

    @Inject
    OrderService orderService;

    public String processUssdInput(UserSession session, String text) {
        
        if (text == null || text.trim().isEmpty()) {
            session.stateHistory.clear();
            session.pushState("main_menu");
            // Track initial session start
            trackingService.trackNavigationAsync(
                session.sessionId, 
                session.phoneNumber, 
                null, 
                "main_menu", 
                NavigationType.DIRECT, 
                null, 
                null, 
                null
            );
            return showMainMenu();
        }

        String[] inputs = text.split("\\*");
        String lastInput = inputs[inputs.length - 1].trim();
        String previousState = session.getCurrentState();

        if ("0".equals(lastInput)) {
            return handleBackNavigation(session);
        }

        String currentState = session.getCurrentState();
        LOG.infof("Processing state: %s, input: %s", currentState, lastInput);

        // Process the input and get response
        String response = switch (currentState) {
            case "main_menu" -> handleMainMenuInput(session, lastInput);
            case "select_camp" -> handleCampSelection(session, lastInput);
            case "select_location" -> handleLocationSelection(session, lastInput);
            case "select_activity" -> handleActivitySelection(session, lastInput);
            case "enter_full_name" -> handleFullNameInput(session, lastInput);
            case "enter_age" -> handleAgeInput(session, lastInput);
            case "enter_guardian_phone" -> handleGuardianPhoneInput(session, lastInput);
            case "enter_participant_phone" -> handleParticipantPhoneInput(session, lastInput);
            case "confirm_registration" -> handleRegistrationConfirmation(session, lastInput);
            case "my_bookings" -> handleMyBookingsInput(session, lastInput);
            case "help" -> handleHelpInput(session, lastInput);
            case "help_menu" -> handleHelpMenuInput(session, lastInput);
            case "how_to_register" -> handleHowToRegisterInput(session, lastInput);
            case "payment_info" -> handlePaymentInfoInput(session, lastInput);
            case "contact_support" -> handleContactSupportInput(session, lastInput);
            default -> "END Invalid session state. Please try again.";
        };

        // Track navigation after processing (if state changed)
        String newState = session.getCurrentState();
        if (!currentState.equals(newState)) {
            NavigationType navType = determineNavigationType(lastInput);
            trackingService.trackNavigationAsync(
                session.sessionId,
                session.phoneNumber,
                previousState,
                newState,
                navType,
                lastInput,
                null, // We don't track time yet
                null  // No additional context for now
            );
        }

        return response;
    }

    private String showMainMenu() {
        return UssdResponseBuilder.create()
            .addLine("Welcome to Camp Sarafrika!")
            .addEmptyLine()
            .addMenuItem(1, "Register for a Camp", null)
            .addMenuItem(2, "My Bookings", null)
            .addMenuItem(3, "Help", null)
            .addMenuItem(4, "Exit", null)
            .build();
    }

    private String handleMainMenuInput(UserSession session, String input) {
        return switch (input) {
            case "1" -> {
                session.pushState("select_camp");
                session.resetPagination();
                yield showCampSelection(session);
            }
            case "2" -> {
                session.pushState("my_bookings");
                yield showMyBookings(session);
            }
            case "3" -> {
                session.pushState("help");
                yield showHelp(session);
            }
            case "4" -> "END Thank you for using Camp Sarafrika services!";
            default -> showInvalidOptionMainMenu();
        };
    }

    private String showCampSelection(UserSession session) {
        List<String> campNames = campService.getDistinctCampNames();
        
        int offset = session.paginationOffset;
        int totalCamps = campNames.size();
        int endIndex = Math.min(offset + PAGE_SIZE, totalCamps);

        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Select a camp:")
            .addEmptyLine();
        
        session.currentMenuItems.clear();

        for (int i = offset; i < endIndex; i++) {
            String campName = campNames.get(i);
            int displayNumber = i - offset + 1;
            builder.addMenuItem(displayNumber, campName, null);
            session.currentMenuItems.add(campName);
        }
        
        if (endIndex < totalCamps) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }

    private String showCategorySelection() {
        List<String> categories = campService.getDistinctCategories();
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Select a category:")
            .addEmptyLine();
        
        for (int i = 0; i < categories.size(); i++) {
            builder.addMenuItem(i + 1, categories.get(i), null);
        }
        
        return builder.addBackOption().build();
    }

    private String handleCategorySelection(UserSession session, String input) {
        try {
            List<String> categories = campService.getDistinctCategories();
            int selection = Integer.parseInt(input) - 1;
            
            if (selection >= 0 && selection < categories.size()) {
                String selectedCategory = categories.get(selection);
                session.putData("selectedCategory", selectedCategory);
                session.pushState("select_camp_type");
                return showCampTypeSelection();
            } else {
                return showInvalidSelectionCategory();
            }
        } catch (NumberFormatException e) {
            return showInvalidInputCategory();
        }
    }

    private String showCampSelection(UserSession session, String category, int offset) {
        List<Camp> camps = campService.getCampsByCategory(category, offset, PAGE_SIZE + 1);
        
        String displayCategory = category;
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine(displayCategory + " Camps:")
            .addEmptyLine();
        
        int displayCount = Math.min(camps.size(), PAGE_SIZE);
        session.currentMenuItems.clear();
        
        for (int i = 0; i < displayCount; i++) {
            Camp camp = camps.get(i);
            
            String cleanCampName = cleanCampName(camp.name, displayCategory);
            
            String locationName = "";
            String fee = "0";
            if (camp.locations != null && !camp.locations.isEmpty()) {
                locationName = camp.locations.get(0).name;
                fee = String.format("%.0f", camp.locations.get(0).fee);
            }
            
            String campDetail = String.format("%s, KSH %s", locationName, fee);
            
            builder.addMenuItem(i + 1, cleanCampName, campDetail);
            session.currentMenuItems.add(camp.uuid.toString());
        }
        
        if (camps.size() > PAGE_SIZE) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }

    private String handleCampSelection(UserSession session, String input) {
        try {
            int selection = Integer.parseInt(input);

            if (selection == 99) { // Handle 'More' option
                session.incrementPagination(PAGE_SIZE);
                return showCampSelection(session);
            }

            int selectedIndex = selection - 1;
            if (selectedIndex >= 0 && selectedIndex < session.currentMenuItems.size()) {
                String selectedCampName = session.currentMenuItems.get(selectedIndex);
                Camp camp = campService.findByName(selectedCampName);
                if (camp != null) {
                    session.putData("selectedCampUuid", camp.uuid.toString());
                    session.pushState("select_location");
                    session.resetPagination(); // Reset for next screen
                    return showLocationSelection(session, camp);
                }
            }
            String campMenu = showCampSelection(session);
            return "CON Invalid selection. Please try again.\n" + campMenu.substring(4);
        } catch (NumberFormatException e) {
            String campMenu = showCampSelection(session);
            return "CON Invalid input. Please enter a number.\n" + campMenu.substring(4);
        }
    }

    private String handleLocationSelection(UserSession session, String input) {
        try {
            String campUuid = session.getStringData("selectedCampUuid");
            Camp camp = campService.findByUuid(UUID.fromString(campUuid));
            
            if (camp == null || camp.locations == null) {
                return "END Camp not found.";
            }
            
            int selection = Integer.parseInt(input);
            
            // Handle pagination
            if (selection == 99) {
                session.incrementPagination(PAGE_SIZE);
                return showLocationSelection(session, camp);
            }
            
            // Handle location selection (1-based input, convert to 0-based index)
            if (selection >= 1 && selection <= session.currentMenuItems.size()) {
                int actualIndex = Integer.parseInt(session.currentMenuItems.get(selection - 1));
                var selectedLocation = camp.locations.get(actualIndex);
                session.putData("selectedLocationId", selectedLocation.uuid);
                session.pushState("select_activity");
                session.resetPagination(); // Reset pagination for next screen
                return showActivitySelection(session, UUID.fromString(campUuid));
            } else {
                return showInvalidSelectionLocation(session, camp);
            }
        } catch (NumberFormatException e) {
            String campUuid = session.getStringData("selectedCampUuid");
            Camp camp = campService.findByUuid(UUID.fromString(campUuid));
            return showInvalidInputLocation(session, camp);
        }
    }

    private String showLocationSelection(UserSession session, Camp camp) {
        if (camp.locations == null || camp.locations.isEmpty()) {
            session.pushState("select_activity");
            return showActivitySelection(session, camp.uuid);
        }
        
        // Use pagination similar to activity selection
        int offset = session.paginationOffset;
        int totalLocations = camp.locations.size();
        int endIndex = Math.min(offset + PAGE_SIZE, totalLocations);
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Select Location:")
            .addEmptyLine();
        
        session.currentMenuItems.clear();
        
        for (int i = offset; i < endIndex; i++) {
            var location = camp.locations.get(i);
            int displayNumber = i - offset + 1; // 1, 2, 3...
            
            // Create a more compact single-line format
            String locationDetail;
            if (location.dates != null && !location.dates.isEmpty() && location.fee != null) {
                // Try compact format: "LocationName (Dates) - KSH Fee"
                String shortDate = truncateDates(location.dates);
                locationDetail = String.format("(%s) KSH %.0f", shortDate, location.fee);
            } else if (location.fee != null) {
                locationDetail = String.format("KSH %.0f", location.fee);
            } else {
                locationDetail = "";
            }
            
            // Use addMenuItem which will handle truncation intelligently
            builder.addMenuItem(displayNumber, location.name, locationDetail);
            session.currentMenuItems.add(String.valueOf(i)); // Store actual index
        }
        
        // Add pagination if there are more locations
        if (endIndex < totalLocations) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }

    private String handleFullNameInput(UserSession session, String input) {
        if (input.length() < 2) {
            return UssdResponseBuilder.create()
                .addLine("Name must be at least 2 characters long.")
                .addLine("Please try again:")
                .addEmptyLine()
                .addBackOption()
                .build();
        }
        
        session.putData("participantName", input);
        session.pushState("enter_age");
        return UssdResponseBuilder.create()
            .addLine("Enter participant's age:")
            .addEmptyLine()
            .addBackOption()
            .build();
    }

    private String handleAgeInput(UserSession session, String input) {
        try {
            int age = Integer.parseInt(input);
            if (age < 5 || age > 18) {
                return UssdResponseBuilder.create()
                    .addLine("Age must be between 5 and 18 years.")
                    .addLine("Please try again:")
                    .addEmptyLine()
                    .addBackOption()
                    .build();
            }
            
            session.putData("participantAge", age);
            
            // All participants are minors (5-18), so always ask for guardian details
            session.pushState("enter_guardian_phone");
            return UssdResponseBuilder.create()
            .addLine("Enter guardian's phone number:")
            .addEmptyLine()
            .addBackOption()
            .build();
            
        } catch (NumberFormatException e) {
            return UssdResponseBuilder.create()
                .addLine("Invalid age. Please enter a valid number:")
                .addEmptyLine()
                .addBackOption()
                .build();
        }
    }

    private String handleGuardianPhoneInput(UserSession session, String input) {
        if (!isValidPhoneNumber(input)) {
            return UssdResponseBuilder.create()
                .addLine("Invalid phone number format.")
                .addLine("Please enter a valid Kenyan phone number:")
                .addEmptyLine()
                .addBackOption()
                .build();
        }
        
        session.putData("guardianPhone", input);
        session.pushState("enter_participant_phone");
        return UssdResponseBuilder.create()
            .addLine("Enter participant's phone number:")
            .addEmptyLine()
            .addBackOption()
            .build();
    }

    private String handleParticipantPhoneInput(UserSession session, String input) {
        if (!isValidPhoneNumber(input)) {
            return UssdResponseBuilder.create()
                .addLine("Invalid phone number format.")
                .addLine("Please enter a valid Kenyan phone number:")
                .addEmptyLine()
                .addBackOption()
                .build();
        }
        
        session.putData("participantPhone", input);
        session.pushState("confirm_registration");
        return showRegistrationConfirmation(session);
    }

    private String showRegistrationConfirmation(UserSession session) {
        String campUuid = session.getStringData("selectedCampUuid");
        Camp camp = campService.findByUuid(UUID.fromString(campUuid));
        
        if (camp == null) {
            return "END Camp not found. Please try again.";
        }
        
        UUID locationUuid = (UUID) session.getData("selectedLocationId");
        String locationName = "";
        String fee = "0";
        String dates = "Not specified";
        
        if (locationUuid != null) {
            var location = locationService.findByUuid(locationUuid);
            if (location != null) {
                locationName = location.name;
                if (location.fee != null) {
                    fee = String.format("%.0f", location.fee);
                }
                if (location.dates != null) {
                    dates = location.dates;
                }
            }
        }
        
        return UssdResponseBuilder.create()
            .addLine("Registration Summary:")
            .addEmptyLine()
            .addLine(String.format("Camp: %s", camp.name))
            .addLine(String.format("Location: %s", locationName))
            .addLine(String.format("Dates: %s", dates))
            .addLine(String.format("Participant: %s", session.getStringData("participantName")))
            .addLine(String.format("Age: %s", session.getStringData("participantAge")))
            .addLine(String.format("Fee: KSH %s", fee))
            .addEmptyLine()
            .addMenuItem(1, "Confirm & Pay", null)
            .addMenuItem(2, "Cancel", null)
            .addBackOption()
            .build();
    }

    private String handleRegistrationConfirmation(UserSession session, String input) {
        return switch (input) {
            case "1" -> processRegistration(session);
            case "2" -> "END Registration cancelled.";
            default -> showInvalidOptionRegistrationConfirmation(session);
        };
    }

    @Transactional
    public String processRegistration(UserSession session) {
        try {
            Registration registration = registrationService.createRegistration(session);
            
            // Get the order that was created with the registration
            var orders = orderService.findByRegistrationUuid(registration.uuid);
            if (orders.isEmpty()) {
                throw new RuntimeException("Order not created for registration");
            }
            
            var order = orders.get(0); // Get the first (and should be only) order
            
            // Send SMS notifications
            String participantPhone = session.getStringData("participantPhone");
            String guardianPhone = session.getStringData("guardianPhone");
            String participantName = session.getStringData("participantName");
            
            // Send confirmation SMS to participant
            smsNotificationService.sendRegistrationConfirmation(
                participantPhone, 
                participantName, 
                registration.camp.name, 
                order.referenceCode
            );
            
            // Send notification SMS to guardian if different phone number
            if (guardianPhone != null && !guardianPhone.equals(participantPhone)) {
                smsNotificationService.sendGuardianNotification(
                    guardianPhone, 
                    participantName, 
                    registration.camp.name, 
                    order.referenceCode
                );
            }
            
            return String.format("""
                END Registration successful!
                
                Reference: %s
                
                Please complete payment via M-Pesa.
                You will receive SMS confirmations.
                
                Thank you for choosing Camp Sarafrika!""",
                order.referenceCode);
                
        } catch (Exception e) {
            LOG.errorf(e, "Failed to create registration for session %s", session.sessionId);
            return "END Registration failed. Please try again later.";
        }
    }

    private String showMyBookings(UserSession session) {
        List<Registration> registrations = registrationService.findByPhoneNumber(session.phoneNumber);
        
        if (registrations.isEmpty()) {
            return "END No bookings found for this number.";
        }
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Your Bookings:")
            .addEmptyLine();
        
        for (int i = 0; i < Math.min(registrations.size(), 3); i++) {
            Registration reg = registrations.get(i);
            
            // Get the order for this registration to show payment status
            var orders = orderService.findByRegistrationUuid(reg.uuid);
            String status = "PENDING";
            String referenceCode = "N/A";
            
            if (!orders.isEmpty()) {
                var order = orders.getFirst();
                // Map OrderStatus to user-friendly terms
                status = switch (order.status) {
                    case PAID -> "CLEARED";
                    case PENDING -> "PENDING";
                    case CONFIRMED -> "CONFIRMED";
                    case CANCELLED -> "CANCELLED";
                    case REFUNDED -> "REFUNDED";
                };
                referenceCode = order.referenceCode;
            }
            
            builder.addMenuItem(i + 1, reg.camp.name, status)
                   .addLine(String.format("   Ref: %s", referenceCode));
        }
        
        return builder.addBackOption().build();
    }

    private String handleMyBookingsInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String showHelp(UserSession session) {
        session.pushState("help_menu");
        return UssdResponseBuilder.create()
            .addLine("Help Menu:")
            .addEmptyLine()
            .addMenuItem(1, "How to Register", null)
            .addMenuItem(2, "Payment Info", null)
            .addMenuItem(3, "Contact Support", null)
            .addBackOption()
            .build();
    }

    private String handleHelpInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String handleHelpMenuInput(UserSession session, String input) {
        return switch (input) {
            case "1" -> {
                session.pushState("how_to_register");
                yield showHowToRegister();
            }
            case "2" -> {
                session.pushState("payment_info");
                yield showPaymentInfo();
            }
            case "3" -> {
                session.pushState("contact_support");
                yield showContactSupport();
            }
            default -> showInvalidOptionHelpMenu();
        };
    }

    private String showHelpMenu() {
        return UssdResponseBuilder.create()
            .addLine("Help Menu:")
            .addEmptyLine()
            .addMenuItem(1, "How to Register", null)
            .addMenuItem(2, "Payment Info", null)
            .addMenuItem(3, "Contact Support", null)
            .addBackOption()
            .build();
    }

    private String showHowToRegister() {
        return UssdResponseBuilder.create()
            .addLine("How to Register:")
            .addEmptyLine()
            .addLine("Dial *789*2020# → 1. Register for a Camp → Select Category → Choose Camp Type → Select Camp → Enter Details → Pay → Done!")
            .addEmptyLine()
            .addBackOption()
            .build();
    }

    private String handleHowToRegisterInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String showPaymentInfo() {
        return UssdResponseBuilder.create()
            .addLine("Payment Info:")
            .addEmptyLine()
            .addLine("Payments are made via Mobile Money. After confirming registration, you will receive an STK Push. Enter your PIN to complete.")
            .addEmptyLine()
            .addBackOption()
            .build();
    }

    private String handlePaymentInfoInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String showContactSupport() {
        return UssdResponseBuilder.create()
            .addLine("Contact Support:")
            .addEmptyLine()
            .addLine("For help, call 0712-345678 or email support.camp@sarafrika.com")
            .addEmptyLine()
            .addBackOption()
            .build();
    }

    private String handleContactSupportInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String handleBackNavigation(UserSession session) {
        String currentState = session.getCurrentState();
        String previousState = session.popState();
        
        // Track back navigation
        trackingService.trackNavigationAsync(
            session.sessionId,
            session.phoneNumber,
            currentState,
            previousState,
            NavigationType.BACK,
            "0",
            null,
            null
        );
        
        return switch (previousState) {
            case "main_menu" -> showMainMenu();
            case "select_category" -> showCategorySelection();
            case "select_camp_type" -> showCampTypeSelection();
            case "select_camp" -> {
                String category = session.getStringData("selectedCategory");
                yield showCampSelection(session, category, session.paginationOffset);
            }
            case "select_location" -> {
                String campUuid = session.getStringData("selectedCampUuid");
                Camp camp = campService.findByUuid(UUID.fromString(campUuid));
                yield showLocationSelection(session, camp);
            }
            case "select_activity" -> {
                UUID campUuid = UUID.fromString(session.getStringData("selectedCampUuid"));
                yield showActivitySelection(session, campUuid);
            }
            case "enter_full_name" -> UssdResponseBuilder.create()
                .addLine("Enter participant's full name:")
                .addEmptyLine()
                .addBackOption()
                .build();
            case "enter_age" -> UssdResponseBuilder.create()
                .addLine("Enter participant's age:")
                .addEmptyLine()
                .addBackOption()
                .build();
            case "enter_guardian_phone" -> UssdResponseBuilder.create()
                .addLine("Enter guardian's phone number:")
                .addEmptyLine()
                .addBackOption()
                .build();
            case "enter_participant_phone" -> UssdResponseBuilder.create()
                .addLine("Enter participant's phone number:")
                .addEmptyLine()
                .addBackOption()
                .build();
            case "confirm_registration" -> showRegistrationConfirmation(session);
            default -> showMainMenu();
        };
    }

    private NavigationType determineNavigationType(String input) {
        if ("0".equals(input)) {
            return NavigationType.BACK;
        } else if ("99".equals(input)) {
            return NavigationType.PAGINATION;
        } else if ("4".equals(input)) {
            return NavigationType.EXIT; // Exit from main menu
        } else {
            return NavigationType.FORWARD;
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        
        String cleaned = phoneNumber.replaceAll("\\s+", "");
        
        return cleaned.matches("^(\\+254|254|0)[17][0-9]{8}$");
    }

    private String showCampTypeSelection() {
        return UssdResponseBuilder.create()
            .addLine("Camp Type:")
            .addEmptyLine()
            .addMenuItem(1, "Half Day: 9:00am - 13:00pm", null)
            .addMenuItem(2, "Boot Camp: Boarding", null)
            .addBackOption()
            .build();
    }

    private String handleCampTypeSelection(UserSession session, String input) {
        return switch (input) {
            case "1" -> {
                session.putData("selectedCampType", "HALF_DAY");
                session.pushState("select_camp");
                session.resetPagination();
                String category = session.getStringData("selectedCategory");
                yield showCampSelection(session, category, 0);
            }
            case "2" -> {
                session.putData("selectedCampType", "BOOT_CAMP");
                session.pushState("select_camp");
                session.resetPagination();
                String category = session.getStringData("selectedCategory");
                yield showCampSelection(session, category, 0);
            }
            default -> showInvalidOptionCampType();
        };
    }

    private String showActivitySelection(UserSession session, UUID campUuid) {
        List<Activity> activities = Activity.findByCampUuid(campUuid).list();
        
        if (activities.isEmpty()) {
            session.pushState("enter_full_name");
            return UssdResponseBuilder.create()
                .addLine("Enter participant's full name:")
                .addEmptyLine()
                .addBackOption()
                .build();
        }
        
        // Use pagination offset similar to location selection
        int offset = session.paginationOffset;
        int totalActivities = activities.size();
        int endIndex = Math.min(offset + PAGE_SIZE, totalActivities);
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Select Activity:")
            .addEmptyLine();
        
        session.currentMenuItems.clear();
        
        for (int i = offset; i < endIndex; i++) {
            Activity activity = activities.get(i);
            int displayNumber = i - offset + 1; // 1, 2, 3...
            builder.addMenuItem(displayNumber, activity.name, null);
            session.currentMenuItems.add(activity.uuid.toString());
        }
        
        // Add pagination if there are more activities
        if (endIndex < totalActivities) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }

    private String handleActivitySelection(UserSession session, String input) {
        try {
            int selection = Integer.parseInt(input);
            UUID campUuid = UUID.fromString(session.getStringData("selectedCampUuid"));
            
            if (selection == 99) {
                session.incrementPagination(PAGE_SIZE);
                return showActivitySelection(session, campUuid);
            }
            
            if (selection >= 1 && selection <= session.currentMenuItems.size()) {
                String activityId = session.currentMenuItems.get(selection - 1);
                session.putData("selectedActivityUuid", activityId);
                session.pushState("enter_full_name");
                return "CON Enter participant's full name:\n\n0. Back";
            } else {
                return showInvalidSelectionActivity(session, campUuid);
            }
        } catch (NumberFormatException e) {
            UUID campUuid = UUID.fromString(session.getStringData("selectedCampUuid"));
            return showInvalidInputActivity(session, campUuid);
        }
    }

    /**
     * Clean camp names by removing redundant category prefixes and making them user-friendly
     */
    private String cleanCampName(String campName, String category) {
        if (campName == null) return "";
        
        String cleaned = campName;
        
        // Remove redundant "YMAC" prefix since category already indicates it
        if (cleaned.startsWith("YMAC ")) {
            cleaned = cleaned.substring(5);
        }
        
        // Remove redundant category prefixes
        if (category.contains("Young Musicians") && cleaned.startsWith("Young Musicians")) {
            cleaned = cleaned.replaceFirst("Young Musicians & Artists Camp", "").trim();
            if (cleaned.startsWith("- ")) cleaned = cleaned.substring(2);
        }
        
        // Clean up common patterns
        cleaned = cleaned.replaceAll("^- ", ""); // Remove leading dash
        cleaned = cleaned.trim();
        
        return cleaned.isEmpty() ? campName : cleaned; // Fallback to original if empty
    }
    
    /**
     * Format location names to be more user-friendly and recognizable
     */
    private String formatLocationForDisplay(String location) {
        if (location == null) return "";
        
        // Map common location abbreviations to full names
        return switch (location.toLowerCase()) {
            case "nairobi" -> "Nairobi";
            case "kiambu" -> "Kiambu";
            case "rongai" -> "Rongai";
            case "karen" -> "Karen";
            case "kisumu" -> "Kisumu";
            case "mombasa" -> "Mombasa";
            case "naivasha" -> "Naivasha";
            case "mount kenya" -> "Mt. Kenya";
            default -> {
                // Capitalize first letter of each word
                String[] words = location.split("\\s+");
                StringBuilder formatted = new StringBuilder();
                for (String word : words) {
                    if (!formatted.isEmpty()) formatted.append(" ");
                    if (!word.isEmpty()) {
                        formatted.append(Character.toUpperCase(word.charAt(0)));
                        if (word.length() > 1) {
                            formatted.append(word.substring(1).toLowerCase());
                        }
                    }
                }
                yield formatted.toString();
            }
        };
    }
    
    /**
     * Truncate long date strings to fit better in USSD constraints
     */
    private String truncateDates(String dates) {
        return dates;
    }
    
    private String showInvalidSelectionLocation(UserSession session, Camp camp) {
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Invalid selection. Please try again.")
            .addEmptyLine();
        
        // Add current location options
        int offset = session.paginationOffset;
        int totalLocations = camp.locations.size();
        int endIndex = Math.min(offset + PAGE_SIZE, totalLocations);
        
        for (int i = offset; i < endIndex; i++) {
            var location = camp.locations.get(i);
            int displayNumber = i - offset + 1;
            String locationDetail = String.format("KSH %.0f", location.fee);
            builder.addMenuItem(displayNumber, location.name, locationDetail);
        }
        
        if (endIndex < totalLocations) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }
    
    private String showInvalidInputLocation(UserSession session, Camp camp) {
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Invalid input. Please enter a number.")
            .addEmptyLine();
        
        // Add current location options
        int offset = session.paginationOffset;
        int totalLocations = camp.locations.size();
        int endIndex = Math.min(offset + PAGE_SIZE, totalLocations);
        
        for (int i = offset; i < endIndex; i++) {
            var location = camp.locations.get(i);
            int displayNumber = i - offset + 1;
            String locationDetail = String.format("KSH %.0f", location.fee);
            builder.addMenuItem(displayNumber, location.name, locationDetail);
        }
        
        if (endIndex < totalLocations) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }
    
    private String showInvalidSelectionActivity(UserSession session, UUID campUuid) {
        List<Activity> activities = Activity.findByCampUuid(campUuid).list();
        int offset = session.paginationOffset;
        int totalActivities = activities.size();
        int endIndex = Math.min(offset + PAGE_SIZE, totalActivities);
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Invalid selection. Please try again.")
            .addEmptyLine();
        
        for (int i = offset; i < endIndex; i++) {
            Activity activity = activities.get(i);
            int displayNumber = i - offset + 1;
            builder.addMenuItem(displayNumber, activity.name, null);
        }
        
        if (endIndex < totalActivities) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }
    
    private String showInvalidInputActivity(UserSession session, UUID campUuid) {
        List<Activity> activities = Activity.findByCampUuid(campUuid).list();
        int offset = session.paginationOffset;
        int totalActivities = activities.size();
        int endIndex = Math.min(offset + PAGE_SIZE, totalActivities);
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Invalid input. Please enter a number.")
            .addEmptyLine();
        
        for (int i = offset; i < endIndex; i++) {
            Activity activity = activities.get(i);
            int displayNumber = i - offset + 1;
            builder.addMenuItem(displayNumber, activity.name, null);
        }
        
        if (endIndex < totalActivities) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }
    

    
    private String showInvalidOptionMainMenu() {
        return UssdResponseBuilder.create()
            .addLine("Invalid option. Please try again.")
            .addEmptyLine()
            .addLine("Welcome to Camp Sarafrika!")
            .addEmptyLine()
            .addMenuItem(1, "Register for a Camp", null)
            .addMenuItem(2, "My Bookings", null)
            .addMenuItem(3, "Help", null)
            .addMenuItem(4, "Exit", null)
            .build();
    }
    
    private String showInvalidSelectionCategory() {
        List<String> categories = campService.getDistinctCategories();
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Invalid selection. Please try again.")
            .addEmptyLine()
            .addLine("Select a category:")
            .addEmptyLine();
        
        for (int i = 0; i < categories.size(); i++) {
            builder.addMenuItem(i + 1, categories.get(i), null);
        }
        
        return builder.addBackOption().build();
    }
    
    private String showInvalidInputCategory() {
        List<String> categories = campService.getDistinctCategories();
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine("Invalid input. Please enter a number.")
            .addEmptyLine()
            .addLine("Select a category:")
            .addEmptyLine();
        
        for (int i = 0; i < categories.size(); i++) {
            builder.addMenuItem(i + 1, categories.get(i), null);
        }
        
        return builder.addBackOption().build();
    }
    
    private String showInvalidOptionRegistrationConfirmation(UserSession session) {
        return UssdResponseBuilder.create()
            .addLine("Invalid option. Please select 1 or 2.")
            .addEmptyLine()
            .addLine("Registration Summary:")
            .addEmptyLine()
            .addMenuItem(1, "Confirm & Pay", null)
            .addMenuItem(2, "Cancel", null)
            .addBackOption()
            .build();
    }
    
    private String showInvalidOptionHelpMenu() {
        return UssdResponseBuilder.create()
            .addLine("Invalid option. Please try again.")
            .addEmptyLine()
            .addLine("Help Menu:")
            .addEmptyLine()
            .addMenuItem(1, "How to Register", null)
            .addMenuItem(2, "Payment Info", null)
            .addMenuItem(3, "Contact Support", null)
            .addBackOption()
            .build();
    }
    
    private String showInvalidOptionCampType() {
        return UssdResponseBuilder.create()
            .addLine("Invalid option. Please try again.")
            .addEmptyLine()
            .addLine("Camp Type:")
            .addEmptyLine()
            .addMenuItem(1, "Half Day: 9:00am - 13:00pm", null)
            .addMenuItem(2, "Boot Camp: Boarding", null)
            .addBackOption()
            .build();
    }
}