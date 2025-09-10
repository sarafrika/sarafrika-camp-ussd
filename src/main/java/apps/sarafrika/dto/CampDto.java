package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Camp data transfer object")
public class CampDto {

    @Schema(description = "Camp UUID", example = "550e8400-e29b-41d4-a716-446655440000", readOnly = true)
    @JsonProperty(value = "uuid", access = Access.READ_ONLY)
    public String uuid;

    @Schema(description = "Camp name", example = "Summer Photography Camp", required = true)
    @JsonProperty("name")
    @NotBlank(message = "Camp name is required")
    public String name;

    @Schema(description = "Camp type (HALF_DAY, BOOT_CAMP)", example = "HALF_DAY", required = true)
    @JsonProperty("camp_type")
    @NotBlank(message = "Camp type is required")
    public String campType;

    @Schema(description = "List of location UUIDs associated with this camp", example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"660e8400-e29b-41d4-a716-446655440001\"]")
    @JsonProperty("location_uuids")
    public List<String> locationUuids;

    @Schema(description = "When the camp was created", readOnly = true)
    @JsonProperty(value = "created_date", access = Access.READ_ONLY)
    public LocalDateTime createdDate;

    @Schema(description = "Who created the camp", readOnly = true)
    @JsonProperty(value = "created_by", access = Access.READ_ONLY)
    public String createdBy;

    @Schema(description = "When the camp was last updated", readOnly = true)
    @JsonProperty(value = "updated_date", access = Access.READ_ONLY)
    public LocalDateTime updatedDate;

    @Schema(description = "Who last updated the camp", readOnly = true)
    @JsonProperty(value = "updated_by", access = Access.READ_ONLY)
    public String updatedBy;
}