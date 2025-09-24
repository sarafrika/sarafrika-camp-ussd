package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CelcomSmsResponse(
    List<SmsResponseItem> responses
) {

    public record SmsResponseItem(
        @JsonProperty("response-code") int responseCode,
        @JsonProperty("response-description") String responseDescription,
        String mobile,
        @JsonProperty("messageid") String messageId,
        @JsonProperty("networkid") int networkId
    ) {}

    // Convenience methods to get the first response item
    public int responseCode() {
        return responses.isEmpty() ? 0 : responses.get(0).responseCode();
    }

    public String responseDescription() {
        return responses.isEmpty() ? "No response" : responses.get(0).responseDescription();
    }

    public String mobile() {
        return responses.isEmpty() ? null : responses.get(0).mobile();
    }

    public String messageId() {
        return responses.isEmpty() ? null : responses.get(0).messageId();
    }

    public int networkId() {
        return responses.isEmpty() ? 0 : responses.get(0).networkId();
    }
}
