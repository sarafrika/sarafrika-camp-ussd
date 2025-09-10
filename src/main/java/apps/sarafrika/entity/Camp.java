package apps.sarafrika.entity;

import apps.sarafrika.enums.CampType;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "camps")
public class Camp extends BaseEntity {

    @Column(name = "name")
    public String name;

    @Column(name = "camp_type")
    public CampType campType;

    @ManyToMany
    @JoinTable(
        name = "camp_locations",
        joinColumns = @JoinColumn(name = "camp_id"),
        inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    public List<Location> locations;

    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Activity> activities;

    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Registration> registrations;

    public static PanacheQuery<Camp> findByName(String name) {
        return find("name = ?1 and isDeleted = false", name);
    }

    public static PanacheQuery<Camp> findAllActive() {
        return find("isDeleted = false");
    }

    public static Camp findByUuid(UUID uuid) {
        return find("uuid = ?1 and isDeleted = false", uuid).firstResult();
    }

    public static List<String> findDistinctNames() {
        return getEntityManager()
                .createQuery("SELECT DISTINCT c.name FROM Camp c WHERE c.isDeleted = false", String.class)
                .getResultList();
    }
}