package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StkPushResponse {

    @JsonProperty("data")
    public StkPushResponseData data;

    @JsonProperty("message")
    public String message;

    @JsonProperty("error")
    public String error;

    public static class StkPushResponseData {
        @JsonProperty("merchantRequestId")
        public String merchantRequestId;

        @JsonProperty("checkoutRequestId")
        public String checkoutRequestId;

        @JsonProperty("responseCode")
        public String responseCode;

        @JsonProperty("responseDescription")
        public String responseDescription;

        @JsonProperty("customerMessage")
        public String customerMessage;
    }
}
