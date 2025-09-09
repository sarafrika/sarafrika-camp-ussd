package apps.sarafrika.entity;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "locations")
public class Location extends BaseEntity {

    @Column(name = "name")
    public String name;

    @Column(name = "fee")
    public BigDecimal fee;

    @ManyToMany(mappedBy = "locations")
    public List<Camp> camps;

    public static PanacheQuery<Location> findAllActive() {
        return find("isDeleted = false");
    }

    public static Location findByUuid(UUID uuid) {
        return find("uuid = ?1 and isDeleted = false", uuid).firstResult();
    }

    public static List<Location> findAll() {
        return find("isDeleted = false").list();
    }
}