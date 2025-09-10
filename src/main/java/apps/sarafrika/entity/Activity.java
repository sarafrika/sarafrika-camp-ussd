package apps.sarafrika.entity;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {

    @Column(name = "name")
    public String name;


    @Column(name = "camp_uuid")
    public UUID campUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camp_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    public Camp camp;

    @Column(name = "is_available")
    public Boolean isAvailable = true;

    public static PanacheQuery<Activity> findByCampUuid(UUID campUuid) {
        return find("campUuid = ?1 and isDeleted = false and isAvailable = true", campUuid);
    }


    public static Activity findByUuid(UUID uuid) {
        return find("uuid = ?1 and isDeleted = false", uuid).firstResult();
    }
}