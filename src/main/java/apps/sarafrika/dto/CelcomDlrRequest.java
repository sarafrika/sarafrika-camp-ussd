package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CelcomDlrRequest(
    @JsonProperty("messageID") String messageId,
    @JsonProperty("deliveryStatus") String deliveryStatus
) {}
