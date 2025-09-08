package apps.sarafrika.entity;

import apps.sarafrika.enums.PaymentStatus;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(name = "payment_reference")
    public String paymentReference;

    @Column(name = "order_uuid")
    public UUID orderUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    public Order order;

    @Column(name = "payment_amount")
    public BigDecimal paymentAmount;

    @Column(name = "payment_status")
    public PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "external_transaction_id")
    public String externalTransactionId;

    @Column(name = "payment_date")
    public LocalDateTime paymentDate;

    @Column(name = "callback_received_date")
    public LocalDateTime callbackReceivedDate;

    public static PanacheQuery<Payment> findByPaymentReference(String paymentReference) {
        return find("paymentReference = ?1 and isDeleted = false", paymentReference);
    }

    public static PanacheQuery<Payment> findByOrderUuid(UUID orderUuid) {
        return find("orderUuid = ?1 and isDeleted = false", orderUuid);
    }

    public static PanacheQuery<Payment> findByStatus(PaymentStatus status) {
        return find("paymentStatus = ?1 and isDeleted = false", status);
    }

    public static PanacheQuery<Payment> findByExternalTransactionId(String externalTransactionId) {
        return find("externalTransactionId = ?1 and isDeleted = false", externalTransactionId);
    }

    public static PanacheQuery<Payment> findAllActive() {
        return find("isDeleted = false");
    }

    public void markAsSuccess(String externalTransactionId) {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.externalTransactionId = externalTransactionId;
        this.paymentDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
        this.updatedDate = LocalDateTime.now();
    }
}