package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Activity data transfer object")
public class ActivityDto {

    @Schema(description = "Activity UUID", example = "550e8400-e29b-41d4-a716-446655440000", readOnly = true)
    @JsonProperty(value = "uuid", access = Access.READ_ONLY)
    public String uuid;

    @Schema(description = "Activity name", example = "Photography Workshop", required = true)
    @JsonProperty("name")
    @NotBlank(message = "Activity name is required")
    public String name;

    @Schema(description = "UUID of the camp this activity belongs to", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    @JsonProperty("camp_uuid")
    @NotNull(message = "Camp UUID is required")
    public String campUuid;

    @Schema(description = "Whether the activity is available for booking", example = "true")
    @JsonProperty("is_available")
    public Boolean isAvailable = true;

    @Schema(description = "When the activity was created", readOnly = true)
    @JsonProperty(value = "created_date", access = Access.READ_ONLY)
    public LocalDateTime createdDate;

    @Schema(description = "Who created the activity", readOnly = true)
    @JsonProperty(value = "created_by", access = Access.READ_ONLY)
    public String createdBy;

    @Schema(description = "When the activity was last updated", readOnly = true)
    @JsonProperty(value = "updated_date", access = Access.READ_ONLY)
    public LocalDateTime updatedDate;

    @Schema(description = "Who last updated the activity", readOnly = true)
    @JsonProperty(value = "updated_by", access = Access.READ_ONLY)
    public String updatedBy;
}