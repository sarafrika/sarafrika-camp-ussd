package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Payment confirmation request containing payment details")
public record PaymentConfirmationRequest(
    @Schema(description = "Order reference code (bill reference) used to match the outstanding order", example = "CS-A1B2C3D4", required = true)
    @JsonProperty("bill_reference")
    @NotBlank(message = "Bill reference cannot be blank")
    String billReference,

    @Schema(description = "Receipt number from the payment provider", example = "RCP123456789", required = true)
    @JsonProperty("receipt_no")
    @NotBlank(message = "Receipt number cannot be blank")
    String receiptNo,

    @Schema(description = "Amount received in the payment", example = "1500.00", required = true)
    @JsonProperty("amount_received")
    @NotNull(message = "Amount received cannot be null")
    @Positive(message = "Amount received must be positive")
    Double amountReceived,

    @Schema(description = "Payment method used", example = "M-PESA", required = true)
    @JsonProperty("payment_method")
    @NotBlank(message = "Payment method cannot be blank")
    String paymentMethod
) {}