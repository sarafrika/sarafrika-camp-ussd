package apps.sarafrika.service;

import apps.sarafrika.entity.Camp;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CampService {

    public List<String> getDistinctCategories() {
        return Camp.findDistinctCategories();
    }

    public List<Camp> getCampsByCategory(String category, int offset, int limit) {
        return Camp.findByCategory(category)
                  .page(Page.of(offset / limit, limit))
                  .list();
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
}