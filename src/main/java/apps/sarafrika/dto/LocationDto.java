package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Location data transfer object")
public class LocationDto {

    @Schema(description = "Location UUID", example = "550e8400-e29b-41d4-a716-446655440000", readOnly = true)
    @JsonProperty(value = "uuid", access = Access.READ_ONLY)
    public String uuid;

    @Schema(description = "Location name", example = "Nairobi", required = true)
    @JsonProperty("name")
    @NotBlank(message = "Location name is required")
    public String name;

    @Schema(description = "Location fee", example = "5000.00", required = true)
    @JsonProperty("fee")
    @NotNull(message = "Location fee is required")
    @Positive(message = "Location fee must be positive")
    public BigDecimal fee;

    @Schema(description = "Available dates for this location", example = "2024-12-01,2024-12-15")
    @JsonProperty("dates")
    public String dates;

    @Schema(description = "When the location was created", readOnly = true)
    @JsonProperty(value = "created_date", access = Access.READ_ONLY)
    public LocalDateTime createdDate;

    @Schema(description = "Who created the location", readOnly = true)
    @JsonProperty(value = "created_by", access = Access.READ_ONLY)
    public String createdBy;

    @Schema(description = "When the location was last updated", readOnly = true)
    @JsonProperty(value = "updated_date", access = Access.READ_ONLY)
    public LocalDateTime updatedDate;

    @Schema(description = "Who last updated the location", readOnly = true)
    @JsonProperty(value = "updated_by", access = Access.READ_ONLY)
    public String updatedBy;
}