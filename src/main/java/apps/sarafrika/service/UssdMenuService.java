package apps.sarafrika.service;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.entity.Camp;
import apps.sarafrika.entity.Registration;
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
            case "select_camp" -> handleCampSelection(session, lastInput);
            case "enter_full_name" -> handleFullNameInput(session, lastInput);
            case "enter_age" -> handleAgeInput(session, lastInput);
            case "enter_guardian_phone" -> handleGuardianPhoneInput(session, lastInput);
            case "enter_participant_phone" -> handleParticipantPhoneInput(session, lastInput);
            case "confirm_registration" -> handleRegistrationConfirmation(session, lastInput);
            case "my_bookings" -> handleMyBookingsInput(session, lastInput);
            case "help" -> handleHelpInput(session, lastInput);
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
                yield showHelp();
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
                session.pushState("select_camp");
                session.resetPagination();
                return showCampSelection(session, selectedCategory, 0);
            } else {
                return "CON Invalid selection. Please try again.\n\n" + showCategorySelection();
            }
        } catch (NumberFormatException e) {
            return "CON Invalid input. Please enter a number.\n\n" + showCategorySelection();
        }
    }

    private String showCampSelection(UserSession session, String category, int offset) {
        List<Camp> camps = campService.getCampsByCategory(category, offset, PAGE_SIZE + 1);
        
        StringBuilder response = new StringBuilder(String.format("CON %s Camps:\n\n", category));
        
        int displayCount = Math.min(camps.size(), PAGE_SIZE);
        session.currentMenuItems.clear();
        
        for (int i = 0; i < displayCount; i++) {
            Camp camp = camps.get(i);
            response.append(String.format("%d. %s\n   %s - KSH %.0f\n", 
                i + 1, camp.name, camp.location, camp.fee));
            session.currentMenuItems.add(camp.uuid.toString());
        }
        
        if (camps.size() > PAGE_SIZE) {
            response.append(String.format("\n%d. More >>\n", PAGE_SIZE + 1));
        }
        
        response.append("\n0. Back");
        return response.toString();
    }

    private String handleCampSelection(UserSession session, String input) {
        try {
            int selection = Integer.parseInt(input);
            String category = session.getStringData("selectedCategory");
            
            if (selection == PAGE_SIZE + 1) {
                session.incrementPagination(PAGE_SIZE);
                return showCampSelection(session, category, session.paginationOffset);
            }
            
            if (selection >= 1 && selection <= session.currentMenuItems.size()) {
                String campId = session.currentMenuItems.get(selection - 1);
                session.putData("selectedCampUuid", campId);
                session.pushState("enter_full_name");
                return "CON Enter participant's full name:\n\n0. Back";
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
            if (age < 5 || age > 25) {
                return "CON Age must be between 5 and 25 years. Please try again:\n\n0. Back";
            }
            
            session.putData("participantAge", age);
            
            if (age < 18) {
                session.pushState("enter_guardian_phone");
                return "CON Participant is under 18. Enter guardian's phone number:\n\n0. Back";
            } else {
                session.pushState("enter_participant_phone");
                return "CON Enter participant's phone number:\n\n0. Back";
            }
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
            
            return String.format("""
                END Registration successful!
                
                Reference: %s
                
                Please complete payment via M-Pesa.
                You will receive an SMS confirmation.
                
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

    private String showHelp() {
        return """
                CON Help & Support:
                
                For assistance:
                - Call: +254 700 000 000
                - Email: help@sarafrika.com
                - SMS: Text 'HELP' to 20000
                
                Operating hours: 8AM - 6PM
                
                0. Back""";
    }

    private String handleHelpInput(UserSession session, String input) {
        return handleBackNavigation(session);
    }

    private String handleBackNavigation(UserSession session) {
        String previousState = session.popState();
        
        return switch (previousState) {
            case "main_menu" -> showMainMenu();
            case "select_category" -> showCategorySelection();
            case "select_camp" -> {
                String category = session.getStringData("selectedCategory");
                yield showCampSelection(session, category, session.paginationOffset);
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
}