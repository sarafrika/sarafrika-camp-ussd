package apps.sarafrika.dto;

import apps.sarafrika.entity.Activity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActivityFactory {

    public ActivityDto fromEntity(Activity activity) {
        if (activity == null) return null;
        
        ActivityDto dto = new ActivityDto();
        dto.uuid = activity.uuid != null ? activity.uuid.toString() : null;
        dto.name = activity.name;
        dto.campUuid = activity.campUuid != null ? activity.campUuid.toString() : null;
        dto.isAvailable = activity.isAvailable;
        dto.createdDate = activity.createdDate;
        dto.createdBy = activity.createdBy;
        dto.updatedDate = activity.updatedDate;
        dto.updatedBy = activity.updatedBy;
        return dto;
    }

    public Activity toEntity(ActivityDto dto) {
        if (dto == null) return null;
        
        Activity activity = new Activity();
        activity.name = dto.name;
        activity.campUuid = dto.campUuid != null ? java.util.UUID.fromString(dto.campUuid) : null;
        activity.isAvailable = dto.isAvailable;
        return activity;
    }

    public List<ActivityDto> fromEntityList(List<Activity> activities) {
        return activities.stream().map(this::fromEntity).collect(Collectors.toList());
    }

    public void updateEntity(Activity activity, ActivityDto dto) {
        if (activity == null || dto == null) return;
        
        activity.name = dto.name;
        if (dto.campUuid != null) {
            activity.campUuid = java.util.UUID.fromString(dto.campUuid);
        }
        activity.isAvailable = dto.isAvailable;
    }
}