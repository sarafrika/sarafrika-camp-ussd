package apps.sarafrika.entity;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
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

    @Column(name = "registration_date")
    public LocalDateTime registrationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Order> orders;

    public static PanacheQuery<Registration> findByParticipantPhone(String phone) {
        return find("participantPhone = ?1 and isDeleted = false", phone);
    }

    public static PanacheQuery<Registration> findAllActive() {
        return find("isDeleted = false");
    }

    public boolean isMinor() {
        return participantAge != null && participantAge < 18;
    }
}