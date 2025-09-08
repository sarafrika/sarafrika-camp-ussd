package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmsRequest {
    
    @JsonProperty("username")
    public String username;
    
    @JsonProperty("to")
    public String to;
    
    @JsonProperty("message")
    public String message;
    
    @JsonProperty("from")
    public String from;

    public SmsRequest() {}

    public SmsRequest(String username, String to, String message, String from) {
        this.username = username;
        this.to = to;
        this.message = message;
        this.from = from;
    }
}