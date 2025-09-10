package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for creating or updating an activity")
public class ActivityRequest {

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
}