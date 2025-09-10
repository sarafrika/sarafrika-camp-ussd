package apps.sarafrika.service;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.entity.Camp;
import apps.sarafrika.entity.Registration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RegistrationService {

    private static final String REFERENCE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

    @Inject
    CampService campService;

    public List<Registration> findByPhoneNumber(String phoneNumber) {
        return Registration.findByParticipantPhone(phoneNumber).list();
    }

    public Registration findByReferenceCode(String referenceCode) {
        return Registration.findByReferenceCode(referenceCode).firstResult();
    }

    public List<Registration> findByStatus(String status) {
        return Registration.findByStatus(status).list();
    }

    @Transactional
    public Registration createRegistration(UserSession session) {
        Registration registration = new Registration();
        
        String campUuid = session.getStringData("selectedCampUuid");
        Camp camp = campService.findByUuid(UUID.fromString(campUuid));
        
        if (camp == null) {
            throw new RuntimeException("Camp not found");
        }
        
        registration.participantName = session.getStringData("participantName");
        registration.participantAge = session.getIntegerData("participantAge");
        registration.participantPhone = session.getStringData("participantPhone");
        registration.guardianPhone = session.getStringData("guardianPhone");
        registration.campUuid = camp.uuid;
        
        // Get fee from the camp's first location (since location-based pricing)
        BigDecimal fee = BigDecimal.ZERO;
        if (camp.locations != null && !camp.locations.isEmpty()) {
            fee = camp.locations.get(0).fee;
        }
        registration.feePaid = fee;
        
        registration.referenceCode = generateReferenceCode();
        registration.status = "PENDING";
        registration.createdBy = "USSD_SYSTEM";
        
        registration.persist();
        return registration;
    }

    @Transactional
    public void markAsPaid(String referenceCode, String paymentReference) {
        Registration registration = findByReferenceCode(referenceCode);
        if (registration != null) {
            registration.markAsPaid();
            registration.updatedBy = "PAYMENT_SYSTEM";
        }
    }

    private String generateReferenceCode() {
        String code;
        do {
            code = "CS-" + generateRandomString(8);
        } while (Registration.findByReferenceCode(code).firstResult() != null);
        
        return code;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(REFERENCE_CHARS.charAt(random.nextInt(REFERENCE_CHARS.length())));
        }
        return sb.toString();
    }
}