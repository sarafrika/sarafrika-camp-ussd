package apps.sarafrika.entity;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registrations")
public class Registration extends BaseEntity {

    @Column(name = "participant_name")
    public String participantName;

    @Column(name = "participant_age")
    public Integer participantAge;

    @Column(name = "participant_phone")
    public String participantPhone;

    @Column(name = "guardian_phone")
    public String guardianPhone;

    @Column(name = "camp_uuid")
    public UUID campUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camp_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    public Camp camp;

    @Column(name = "selected_activity")
    public String selectedActivity;

    @Column(name = "fee_paid")
    public BigDecimal feePaid;

    @Column(name = "reference_code")
    public String referenceCode;

    @Column(name = "status")
    public String status = "PENDING";

    @Column(name = "registration_date")
    public LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "payment_date")
    public LocalDateTime paymentDate;

    public static PanacheQuery<Registration> findByReferenceCode(String referenceCode) {
        return find("referenceCode = ?1 and isDeleted = false", referenceCode);
    }

    public static PanacheQuery<Registration> findByParticipantPhone(String phone) {
        return find("participantPhone = ?1 and isDeleted = false", phone);
    }

    public static PanacheQuery<Registration> findByStatus(String status) {
        return find("status = ?1 and isDeleted = false", status);
    }

    public boolean isMinor() {
        return participantAge != null && participantAge < 18;
    }

    public void markAsPaid() {
        this.status = "PAID";
        this.paymentDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }
}