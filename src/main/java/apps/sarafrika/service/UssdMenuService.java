package apps.sarafrika.service;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.entity.Activity;
import apps.sarafrika.entity.Camp;
import apps.sarafrika.entity.Registration;
import apps.sarafrika.enums.CampType;
import apps.sarafrika.enums.NavigationType;
import apps.sarafrika.service.TrackingService;
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

    public String processUssdInput(UserSession session, String text) {
        
        if (text == null || text.trim().isEmpty()) {
            session.stateHistory.clear();
            session.pushState("main_menu");
            return showMainMenu();
        }

        String[] inputs = text.split("\\*");
        String lastInput = inputs[inputs.length - 1].trim();

        if ("0".equals(lastInput)) {
            return handleBackNavigation(session);
        }

        String currentState = session.getCurrentState();
        LOG.infof("Processing state: %s, input: %s", currentState, lastInput);

        return switch (currentState) {
            case "main_menu" -> handleMainMenuInput(session, lastInput);
            case "select_category" -> handleCategorySelection(session, lastInput);
            case "select_camp_type" -> handleCampTypeSelection(session, lastInput);
            case "select_camp" -> handleCampSelection(session, lastInput);
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
    }

    private String showMainMenu() {
        return """
                CON Welcome to Camp Sarafrika!
                
                1. Register for a Camp
                2. My Bookings
                3. Help
                4. Exit""";
    }

    private String handleMainMenuInput(UserSession session, String input) {
        return switch (input) {
            case "1" -> {
                session.pushState("select_category");
                session.resetPagination();
                yield showCategorySelection();
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
            default -> "CON Invalid option. Please try again.\n\n" + showMainMenu();
        };
    }

    private String showCategorySelection() {
        List<String> categories = campService.getDistinctCategories();
        
        StringBuilder response = new StringBuilder("CON Select a camp category:\n\n");
        
        for (int i = 0; i < categories.size(); i++) {
            response.append(String.format("%d. %s\n", i + 1, categories.get(i)));
        }
        
        response.append("\n0. Back");
        return response.toString();
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
                return "CON Invalid selection. Please try again.\n\n" + showCategorySelection();
            }
        } catch (NumberFormatException e) {
            return "CON Invalid input. Please enter a number.\n\n" + showCategorySelection();
        }
    }

    private String showCampSelection(UserSession session, String category, int offset) {
        List<Camp> camps = campService.getCampsByCategory(category, offset, PAGE_SIZE + 1);
        
        // Truncate long category names for USSD display
        String displayCategory = category.length() > 20 ? 
            category.substring(0, 17) + "..." : category;
        
        UssdResponseBuilder builder = UssdResponseBuilder.create()
            .addLine(displayCategory + " Camps:")
            .addEmptyLine();
        
        int displayCount = Math.min(camps.size(), PAGE_SIZE);
        session.currentMenuItems.clear();
        
        for (int i = 0; i < displayCount; i++) {
            Camp camp = camps.get(i);
            
            // Clean up camp name - remove redundant category prefixes
            String cleanCampName = cleanCampName(camp.name, displayCategory);
            
            // Format location for better understanding
            String readableLocation = formatLocationForDisplay(camp.location);
            String campDetail = String.format("%s, KSH %.0f", readableLocation, camp.fee);
            
            // Check if adding this camp would exceed limits
            if (!builder.wouldExceedLimit(String.format("%d. %s - %s", i + 1, cleanCampName, campDetail))) {
                builder.addMenuItem(i + 1, cleanCampName, campDetail);
                session.currentMenuItems.add(camp.uuid.toString());
            } else {
                // If we can't fit, prioritize showing location over long names
                String shortName = cleanCampName.length() > 20 ? 
                    cleanCampName.substring(0, 17) + "..." : cleanCampName;
                String shortDetail = String.format("%s, KSH %.0f", 
                    readableLocation.length() > 10 ? readableLocation.substring(0, 7) + "..." : readableLocation, 
                    camp.fee);
                builder.addMenuItem(i + 1, shortName, shortDetail);
                session.currentMenuItems.add(camp.uuid.toString());
            }
        }
        
        if (camps.size() > PAGE_SIZE) {
            builder.addMoreOption();
        }
        
        return builder.addBackOption().build();
    }

    private String handleCampSelection(UserSession session, String input) {
        try {
            int selection = Integer.parseInt(input);
            String category = session.getStringData("selectedCategory");
            
            if (selection == 99) {
                session.incrementPagination(PAGE_SIZE);
                return showCampSelection(session, category, session.paginationOffset);
            }
            
            if (selection >= 1 && selection <= session.currentMenuItems.size()) {
                String campId = session.currentMenuItems.get(selection - 1);
                session.putData("selectedCampUuid", campId);
                session.pushState("select_activity");
                session.resetPagination();
                return showActivitySelection(session, UUID.fromString(campId));
            } else {
                return "CON Invalid selection. Please try again.\n\n" + 
                       showCampSelection(session, category, session.paginationOffset);
            }
        } catch (NumberFormatException e) {
            String category = session.getStringData("selectedCategory");
            return "CON Invalid input. Please enter a number.\n\n" + 
                   showCampSelection(session, category, session.paginationOffset);
        }
    }

    private String handleFullNameInput(UserSession session, String input) {
        if (input.length() < 2) {
            return "CON Name must be at least 2 characters long. Please try again:\n\n0. Back";
        }
        
        session.putData("participantName", input);
        session.pushState("enter_age");
        return "CON Enter participant's age:\n\n0. Back";
    }

    private String handleAgeInput(UserSession session, String input) {
        try {
            int age = Integer.parseInt(input);
            if (age < 5 || age > 18) {
                return "CON Age must be between 5 and 18 years. Please try again:\n\n0. Back";
            }
            
            session.putData("participantAge", age);
            
            // All participants are minors (5-18), so always ask for guardian details
            session.pushState("enter_guardian_phone");
            return "CON Enter guardian's phone number:\n\n0. Back";
            
        } catch (NumberFormatException e) {
            return "CON Invalid age. Please enter a valid number:\n\n0. Back";
        }
    }

    private String handleGuardianPhoneInput(UserSession session, String input) {
        if (!isValidPhoneNumber(input)) {
            return "CON Invalid phone number format. Please enter a valid Kenyan phone number:\n\n0. Back";
        }
        
        session.putData("guardianPhone", input);
        session.pushState("enter_participant_phone");
        return "CON Enter participant's phone number:\n\n0. Back";
    }

    private String handleParticipantPhoneInput(UserSession session, String input) {
        if (!isValidPhoneNumber(input)) {
            return "CON Invalid phone number format. Please enter a valid Kenyan phone number:\n\n0. Back";
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
        
        StringBuilder response = new StringBuilder("CON Registration Summary:\n\n");
        response.append(String.format("Camp: %s\n", camp.name));
        response.append(String.format("Location: %s\n", camp.location));
        response.append(String.format("Dates: %s\n", camp.dates));
        response.append(String.format("Participant: %s\n", session.getStringData("participantName")));
        response.append(String.format("Age: %s\n", session.getStringData("participantAge")));
        response.append(String.format("Fee: KSH %.0f\n\n", camp.fee));
        response.append("1. Confirm & Pay\n");
        response.append("2. Cancel\n\n");
        response.append("0. Back");
        
        return response.toString();
    }

    private String handleRegistrationConfirmation(UserSession session, String input) {
        return switch (input) {
            case "1" -> processRegistration(session);
            case "2" -> "END Registration cancelled.";
            default -> "CON Invalid option. Please select 1 or 2.\n\n" + showRegistrationConfirmation(session);
        };
    }

    @Transactional
    public String processRegistration(UserSession session) {
        try {
            Registration registration = registrationService.createRegistration(session);
            
            // Send SMS notifications
            String participantPhone = session.getStringData("participantPhone");
            String guardianPhone = session.getStringData("guardianPhone");
            String participantName = session.getStringData("participantName");
            
            // Send confirmation SMS to participant
            smsNotificationService.sendRegistrationConfirmation(
                participantPhone, 
                participantName, 
                registration.camp.name, 
                registration.referenceCode
            );
            
            // Send notification SMS to guardian if different phone number
            if (guardianPhone != null && !guardianPhone.equals(participantPhone)) {
                smsNotificationService.sendGuardianNotification(
                    guardianPhone, 
                    participantName, 
                    registration.camp.name, 
                    registration.referenceCode
                );
            }
            
            return String.format("""
                END Registration successful!
                
                Reference: %s
                
                Please complete payment via M-Pesa.
                You will receive SMS confirmations.
                
                Thank you for choosing Camp Sarafrika!""",
                registration.referenceCode);
                
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
        
        StringBuilder response = new StringBuilder("CON Your Bookings:\n\n");
        
        for (int i = 0; i < Math.min(registrations.size(), 3); i++) {
            Registration reg = registrations.get(i);
            response.append(String.format("%d. %s - %s\n   Ref: %s\n", 
                i + 1, reg.camp.name, reg.status, reg.referenceCode));
        }
        
        response.append("\n0. Back");
        return response.toString();
    }

    private String handleMyBookingsInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String showHelp(UserSession session) {
        session.pushState("help_menu");
        return """
                CON Help Menu:
                
                1. How to Register
                2. Payment Info
                3. Contact Support
                
                0. Back""";
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
            default -> "CON Invalid option. Please try again.\n\n" + showHelpMenu();
        };
    }

    private String showHelpMenu() {
        return """
                CON Help Menu:
                
                1. How to Register
                2. Payment Info
                3. Contact Support
                
                0. Back""";
    }

    private String showHowToRegister() {
        return """
                CON How to Register:
                
                Dial *123# → 1. Register for a Camp → Select Category → Choose Camp Type → Select Camp → Enter Details → Pay → Done!
                
                0. Back""";
    }

    private String handleHowToRegisterInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String showPaymentInfo() {
        return """
                CON Payment Info:
                
                Payments are made via Mobile Money. After confirming registration, you will receive an STK Push. Enter your PIN to complete.
                
                0. Back""";
    }

    private String handlePaymentInfoInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String showContactSupport() {
        return """
                CON Contact Support:
                
                For help, call 0712-345678 or email support.camp@sarafrika.com
                
                0. Back""";
    }

    private String handleContactSupportInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String handleBackNavigation(UserSession session) {
        String previousState = session.popState();
        
        return switch (previousState) {
            case "main_menu" -> showMainMenu();
            case "select_category" -> showCategorySelection();
            case "select_camp_type" -> showCampTypeSelection();
            case "select_camp" -> {
                String category = session.getStringData("selectedCategory");
                yield showCampSelection(session, category, session.paginationOffset);
            }
            case "select_activity" -> {
                UUID campUuid = UUID.fromString(session.getStringData("selectedCampUuid"));
                yield showActivitySelection(session, campUuid);
            }
            case "enter_full_name" -> "CON Enter participant's full name:\n\n0. Back";
            case "enter_age" -> "CON Enter participant's age:\n\n0. Back";
            case "enter_guardian_phone" -> "CON Enter guardian's phone number:\n\n0. Back";
            case "enter_participant_phone" -> "CON Enter participant's phone number:\n\n0. Back";
            case "confirm_registration" -> showRegistrationConfirmation(session);
            default -> showMainMenu();
        };
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        
        String cleaned = phoneNumber.replaceAll("\\s+", "");
        
        return cleaned.matches("^(\\+254|254|0)[17][0-9]{8}$");
    }

    private String showCampTypeSelection() {
        return """
                CON Camp Type:
                
                1. Half Day: 9:00am - 13:00pm
                2. Boot Camp: Boarding
                
                0. Back""";
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
            default -> "CON Invalid option. Please try again.\n\n" + showCampTypeSelection();
        };
    }

    private String showActivitySelection(UserSession session, UUID campUuid) {
        List<Activity> activities = Activity.findByCampUuid(campUuid).list();
        
        if (activities.isEmpty()) {
            session.pushState("enter_full_name");
            return "CON Enter participant's full name:\n\n0. Back";
        }
        
        StringBuilder response = new StringBuilder("CON Select Activity:\n\n");
        session.currentMenuItems.clear();
        
        int displayCount = Math.min(activities.size(), PAGE_SIZE);
        for (int i = 0; i < displayCount; i++) {
            Activity activity = activities.get(i);
            response.append(String.format("%d. %s\n", i + 1, activity.name));
            session.currentMenuItems.add(activity.uuid.toString());
        }
        
        if (activities.size() > PAGE_SIZE) {
            response.append("\n99. More >>\n");
        }
        
        response.append("\n0. Back");
        return response.toString();
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
                return "CON Invalid selection. Please try again.\n\n" + 
                       showActivitySelection(session, campUuid);
            }
        } catch (NumberFormatException e) {
            UUID campUuid = UUID.fromString(session.getStringData("selectedCampUuid"));
            return "CON Invalid input. Please enter a number.\n\n" + 
                   showActivitySelection(session, campUuid);
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
                    if (formatted.length() > 0) formatted.append(" ");
                    if (word.length() > 0) {
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
}