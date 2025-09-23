package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CelcomSmsResponse(
    @JsonProperty("response-code") int responseCode,
    @JsonProperty("response-description") String responseDescription,
    String mobile,
    @JsonProperty("messageid") String messageId,
    @JsonProperty("networkid") String networkId
) {}
