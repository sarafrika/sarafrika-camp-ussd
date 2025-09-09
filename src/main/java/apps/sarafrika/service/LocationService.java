package apps.sarafrika.service;

import apps.sarafrika.entity.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LocationService {

    public List<Location> findAllActive() {
        return Location.findAllActive().list();
    }

    public Location findByUuid(UUID uuid) {
        return Location.findByUuid(uuid);
    }

    @Transactional
    public Location createLocation(Location location) {
        location.persist();
        return location;
    }

    @Transactional
    public Location updateLocation(Location location) {
        return Location.getEntityManager().merge(location);
    }

    @Transactional
    public void deleteLocation(UUID uuid, String deletedBy) {
        Location location = Location.findByUuid(uuid);
        if (location != null) {
            location.softDelete(deletedBy);
        }
    }
}