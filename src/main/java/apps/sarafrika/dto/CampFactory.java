package apps.sarafrika.dto;

import apps.sarafrika.entity.Camp;
import apps.sarafrika.enums.CampType;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CampFactory {

    public CampDto fromEntity(Camp camp) {
        if (camp == null) return null;
        
        CampDto dto = new CampDto();
        dto.uuid = camp.uuid != null ? camp.uuid.toString() : null;
        dto.name = camp.name;
        dto.campType = camp.campType != null ? camp.campType.name() : null;
        dto.createdDate = camp.createdDate;
        dto.createdBy = camp.createdBy;
        dto.updatedDate = camp.updatedDate;
        dto.updatedBy = camp.updatedBy;
        return dto;
    }

    public Camp toEntity(CampDto dto) {
        if (dto == null) return null;
        
        Camp camp = new Camp();
        camp.name = dto.name;
        camp.campType = dto.campType != null ? CampType.valueOf(dto.campType) : null;
        return camp;
    }

    public List<CampDto> fromEntityList(List<Camp> camps) {
        return camps.stream().map(this::fromEntity).collect(Collectors.toList());
    }

    public void updateEntity(Camp camp, CampDto dto) {
        if (camp == null || dto == null) return;
        
        camp.name = dto.name;
        if (dto.campType != null) {
            camp.campType = CampType.valueOf(dto.campType);
        }
    }
}