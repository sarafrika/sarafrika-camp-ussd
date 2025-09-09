package apps.sarafrika.service;

import apps.sarafrika.entity.Activity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ActivityService {

    public List<Activity> getActivitiesByCamp(UUID campUuid) {
        return Activity.findByCampUuid(campUuid).list();
    }

    public List<Activity> getActivitiesByCategory(String category) {
        return Activity.findByCategory(category).list();
    }

    public Activity findByUuid(UUID uuid) {
        return Activity.findByUuid(uuid);
    }

    public List<Activity> findAllActive() {
        return Activity.find("isDeleted = false and isAvailable = true").list();
    }

    @Transactional
    public Activity createActivity(Activity activity) {
        activity.persist();
        return activity;
    }

    @Transactional
    public Activity updateActivity(Activity activity) {
        return Activity.getEntityManager().merge(activity);
    }

    @Transactional
    public void deleteActivity(UUID uuid, String deletedBy) {
        Activity activity = Activity.findByUuid(uuid);
        if (activity != null) {
            activity.softDelete(deletedBy);
        }
    }

    public List<String> getDistinctCategories() {
        return Activity.findDistinctCategories();
    }
}