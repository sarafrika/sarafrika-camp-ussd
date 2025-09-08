package apps.sarafrika.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "uuid")
    public UUID uuid = UUID.randomUUID();

    @Column(name = "created_date")
    public LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "created_by")
    public String createdBy;

    @Column(name = "updated_date")
    public LocalDateTime updatedDate = LocalDateTime.now();

    @Column(name = "updated_by")
    public String updatedBy;

    @Column(name = "is_deleted")
    public Boolean isDeleted = false;

    @Column(name = "deleted_by")
    public String deletedBy;

    @Column(name = "deleted_date")
    public LocalDateTime deletedDate;

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    public void softDelete(String deletedBy) {
        this.isDeleted = true;
        this.deletedBy = deletedBy;
        this.deletedDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        this.updatedBy = deletedBy;
    }
}