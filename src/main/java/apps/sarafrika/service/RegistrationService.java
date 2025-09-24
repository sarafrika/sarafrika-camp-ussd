package apps.sarafrika.service;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.entity.Camp;
import apps.sarafrika.entity.Order;
import apps.sarafrika.entity.Registration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RegistrationService {

    @Inject
    CampService campService;
    
    @Inject
    OrderService orderService;
    
    @Inject
    LocationService locationService;

    public List<Registration> findByPhoneNumber(String phoneNumber) {
        return Registration.findByParticipantPhone(phoneNumber).list();
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
        registration.createdBy = "USSD_SYSTEM";
        
        registration.persist();
        
        // Get activity and location from session for order creation
        String activityUuidStr = session.getStringData("selectedActivityUuid");
        UUID activityUuid = activityUuidStr != null ? UUID.fromString(activityUuidStr) : null;
        String locationUuidStr = session.getStringData("selectedLocationId");
        UUID locationUuid = locationUuidStr != null ? UUID.fromString(locationUuidStr) : null;
        
        // Create order for payment tracking with activity and location
        BigDecimal orderAmount = BigDecimal.ZERO;
        if (locationUuid != null) {
            var location = locationService.findByUuid(locationUuid);
            if (location != null && location.fee != null) {
                orderAmount = location.fee;
            }
        }
        
        Order order = orderService.createOrder(registration, activityUuid, locationUuid, orderAmount);
        
        registration.camp = camp;
        return registration;
    }

    public Order findOrderByReferenceCode(String referenceCode) {
        return orderService.findByReferenceCode(referenceCode);
    }

    @Transactional
    public void markOrderAsPaid(String referenceCode, String paymentReference) {
        orderService.markAsPaid(referenceCode, paymentReference);
    }
}