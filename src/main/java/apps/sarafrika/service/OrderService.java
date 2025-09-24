package apps.sarafrika.service;

import apps.sarafrika.entity.Order;
import apps.sarafrika.entity.Registration;
import apps.sarafrika.entity.Activity;
import apps.sarafrika.entity.Location;
import apps.sarafrika.enums.OrderStatus;
import apps.sarafrika.service.SmsNotificationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderService {

    private static final Logger LOG = Logger.getLogger(OrderService.class);
    private static final String REFERENCE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

    @Inject
    SmsNotificationService smsNotificationService;

    public List<Order> findByRegistrationUuid(UUID registrationUuid) {
        return Order.findByRegistrationUuid(registrationUuid).list();
    }

    public Order findByReferenceCode(String referenceCode) {
        return Order.findByReferenceCode(referenceCode).firstResult();
    }

    public List<Order> findByStatus(OrderStatus status) {
        return Order.findByStatus(status).list();
    }

    public List<Order> findByPhoneNumber(String phoneNumber) {
        // Find orders through registrations by phone number
        return Order.find(
            "SELECT o FROM Order o JOIN o.registration r WHERE r.participantPhone = ?1 AND o.isDeleted = false ORDER BY o.orderDate DESC", 
            phoneNumber
        ).list();
    }

    @Transactional
    public Order createOrder(Registration registration, UUID activityUuid, UUID locationUuid, BigDecimal amount) {
        Order order = new Order();
        
        order.registrationUuid = registration.uuid;
        order.selectedActivityUuid = activityUuid;
        order.selectedLocationUuid = locationUuid;
        order.orderAmount = amount;
        order.referenceCode = generateReferenceCode();
        order.status = OrderStatus.PENDING;
        order.createdBy = "USSD_SYSTEM";
        
        order.persist();
        return order;
    }

    @Transactional
    public void markAsPaid(String referenceCode, String paymentReference) {
        Order order = findByReferenceCode(referenceCode);
        if (order != null) {
            order.markAsPaid();
            order.updatedBy = "PAYMENT_SYSTEM";
        }
    }

    @Transactional
    public void markAsPaidWithNotification(Order order, BigDecimal amountPaid, String updatedBy) {
        // Mark order as paid
        order.markAsPaid();
        order.updatedBy = updatedBy;

        // Send payment confirmation SMS
        sendPaymentConfirmationSms(order, amountPaid);
    }

    private void sendPaymentConfirmationSms(Order order, BigDecimal amountPaid) {
        try {
            Activity activity = Activity.findByUuid(order.selectedActivityUuid);
            Location location = Location.findByUuid(order.selectedLocationUuid);

            if (order.registration != null && activity != null && location != null) {
                String phoneNumber = order.registration.participantPhone;
                String participantName = order.registration.participantName;
                String activityName = activity.name;
                String locationName = location.name;
                String locationDates = location.dates != null ? location.dates : "";
                String referenceCode = order.referenceCode;
                double amount = amountPaid.doubleValue();

                boolean smsSent = smsNotificationService.sendPaymentConfirmation(
                    phoneNumber, participantName, activityName, locationName,
                    locationDates, referenceCode, amount
                );

                if (smsSent) {
                    LOG.infof("Payment confirmation SMS sent for order %s", order.referenceCode);
                } else {
                    LOG.warnf("Failed to send payment confirmation SMS for order %s", order.referenceCode);
                }
            } else {
                LOG.warnf("Cannot send SMS for order %s - missing registration, activity, or location data", order.referenceCode);
            }
        } catch (Exception e) {
            LOG.errorf(e, "Error sending payment confirmation SMS for order %s", order.referenceCode);
        }
    }

    private String generateReferenceCode() {
        String code;
        do {
            code = "CS-" + generateRandomString(8);
        } while (Order.findByReferenceCode(code).firstResult() != null);
        
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