package apps.sarafrika.service;

import apps.sarafrika.entity.Order;
import apps.sarafrika.entity.Registration;
import apps.sarafrika.enums.OrderStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderService {

    private static final String REFERENCE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

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
    public Order createOrder(Registration registration, BigDecimal amount) {
        Order order = new Order();
        
        order.registrationUuid = registration.uuid;
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