package apps.sarafrika.service;

import apps.sarafrika.entity.Camp;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CampService {

    public List<String> getDistinctCampNames() {
        return Camp.findDistinctNames();
    }

    public Camp findByName(String name) {
        return Camp.findByName(name).firstResult();
    }

    public Camp findByUuid(UUID uuid) {
        return Camp.findByUuid(uuid);
    }

    public List<Camp> findAllActive() {
        return Camp.findAllActive().list();
    }

    @Transactional
    public Camp createCamp(Camp camp) {
        camp.persist();
        return camp;
    }

    @Transactional
    public Camp updateCamp(Camp camp) {
        return Camp.getEntityManager().merge(camp);
    }

    @Transactional
    public void deleteCamp(UUID uuid, String deletedBy) {
        Camp camp = Camp.findByUuid(uuid);
        if (camp != null) {
            camp.softDelete(deletedBy);
        }
    }

    public List<String> getDistinctCategories() {
        // Camp categories are the camp names themselves
        return getDistinctCampNames();
    }

    public List<Camp> getCampsByCategory(String category, int offset, int limit) {
        // Since categories are camp names, find camps by name
        return Camp.find("name = ?1 and isDeleted = false", category)
                .page(Page.of(offset / limit, limit))
                .list();
    }

    public List<Camp> getCampsByCategory(String category) {
        // Since categories are camp names, find camps by name without pagination
        return Camp.find("name = ?1 and isDeleted = false", category).list();
    }
}