package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CelcomSmsRequest(
    @JsonProperty("partnerID") String partnerID,
    @JsonProperty("apikey") String apikey,
    String mobile,
    String message,
    String shortcode
) {}
