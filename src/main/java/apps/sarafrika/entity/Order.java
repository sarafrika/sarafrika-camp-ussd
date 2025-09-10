package apps.sarafrika.entity;

import apps.sarafrika.enums.OrderStatus;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "reference_code", unique = true)
    public String referenceCode;

    @Column(name = "registration_uuid")
    public UUID registrationUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    public Registration registration;

    @Column(name = "selected_activity_uuid")
    public UUID selectedActivityUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_activity_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    public Activity selectedActivity;

    @Column(name = "selected_location_uuid")
    public UUID selectedLocationUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_location_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    public Location selectedLocation;

    @Column(name = "order_amount")
    public BigDecimal orderAmount;

    @Column(name = "status")
    public OrderStatus status = OrderStatus.PENDING;

    @Column(name = "order_date")
    public LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "payment_date")
    public LocalDateTime paymentDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Payment> payments;

    public static PanacheQuery<Order> findByReferenceCode(String referenceCode) {
        return find("referenceCode = ?1 and isDeleted = false", referenceCode);
    }

    public static PanacheQuery<Order> findByStatus(OrderStatus status) {
        return find("status = ?1 and isDeleted = false", status);
    }

    public static PanacheQuery<Order> findByRegistrationUuid(UUID registrationUuid) {
        return find("registrationUuid = ?1 and isDeleted = false", registrationUuid);
    }

    public static PanacheQuery<Order> findAllActive() {
        return find("isDeleted = false");
    }

    public void markAsPaid() {
        this.status = OrderStatus.PAID;
        this.paymentDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    public boolean isPaid() {
        return OrderStatus.PAID.equals(this.status);
    }

    public boolean isPending() {
        return OrderStatus.PENDING.equals(this.status);
    }
}