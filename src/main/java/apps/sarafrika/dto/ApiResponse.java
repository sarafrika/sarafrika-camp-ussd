package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "Indicates if the request was successful", example = "true")
    @JsonProperty("success")
    public boolean success;
    
    @Schema(description = "Response data when successful")
    @JsonProperty("data")
    public T data;
    
    @Schema(description = "Error message when unsuccessful")
    @JsonProperty("error")
    public String error;
    
    @Schema(description = "Additional error details")
    @JsonProperty("details")
    public String details;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> error(String error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = error;
        return response;
    }

    public static <T> ApiResponse<T> error(String error, String details) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = error;
        response.details = details;
        return response;
    }
}