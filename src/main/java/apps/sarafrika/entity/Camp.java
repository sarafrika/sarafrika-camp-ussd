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

    @Column(name = "category")
    public String category;

    @Column(name = "camp_type")
    public CampType campType;

    @Column(name = "location")
    public String location;

    @Column(name = "dates")
    public String dates;

    @Column(name = "fee")
    public BigDecimal fee;

    @Column(name = "activities", columnDefinition = "jsonb")
    public String activities;

    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Registration> registrations;

    public static PanacheQuery<Camp> findByCategory(String category) {
        return find("category = ?1 and isDeleted = false", category);
    }

    public static PanacheQuery<Camp> findActiveByCategory(String category) {
        return find("category = ?1 and isDeleted = false", category);
    }

    public static PanacheQuery<Camp> findAllActive() {
        return find("isDeleted = false");
    }

    public static Camp findByUuid(UUID uuid) {
        return find("uuid = ?1 and isDeleted = false", uuid).firstResult();
    }

    public static List<String> findDistinctCategories() {
        return getEntityManager()
                .createQuery("SELECT DISTINCT c.category FROM Camp c WHERE c.isDeleted = false", String.class)
                .getResultList();
    }
}