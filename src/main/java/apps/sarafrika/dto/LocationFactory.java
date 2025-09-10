package apps.sarafrika.dto;

import apps.sarafrika.entity.Location;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LocationFactory {

    public LocationDto fromEntity(Location location) {
        if (location == null) return null;
        
        LocationDto dto = new LocationDto();
        dto.uuid = location.uuid != null ? location.uuid.toString() : null;
        dto.name = location.name;
        dto.fee = location.fee;
        dto.dates = location.dates;
        dto.createdDate = location.createdDate;
        dto.createdBy = location.createdBy;
        dto.updatedDate = location.updatedDate;
        dto.updatedBy = location.updatedBy;
        return dto;
    }

    public Location toEntity(LocationDto dto) {
        if (dto == null) return null;
        
        Location location = new Location();
        location.name = dto.name;
        location.fee = dto.fee;
        location.dates = dto.dates;
        return location;
    }

    public List<LocationDto> fromEntityList(List<Location> locations) {
        return locations.stream().map(this::fromEntity).collect(Collectors.toList());
    }

    public void updateEntity(Location location, LocationDto dto) {
        if (location == null || dto == null) return;
        
        location.name = dto.name;
        location.fee = dto.fee;
        location.dates = dto.dates;
    }
}