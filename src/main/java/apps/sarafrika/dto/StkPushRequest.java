package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record StkPushRequest(
    @JsonProperty("phoneNo") String phoneNo,
    @JsonProperty("amount") BigDecimal amount,
    @JsonProperty("paybill") String paybill,
    @JsonProperty("paymentReference") String paymentReference
) {}
