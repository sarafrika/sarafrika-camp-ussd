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

    @Column(name = "order_code")
    public String orderCode;

    @Column(name = "registration_uuid")
    public UUID registrationUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    public Registration registration;

    @Column(name = "order_amount")
    public BigDecimal orderAmount;

    @Column(name = "status")
    public OrderStatus status = OrderStatus.PENDING;

    @Column(name = "order_date")
    public LocalDateTime orderDate = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Payment> payments;

    public static PanacheQuery<Order> findByOrderCode(String orderCode) {
        return find("orderCode = ?1 and isDeleted = false", orderCode);
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
}