package apps.sarafrika.controller;

import apps.sarafrika.dto.ApiResponse;
import apps.sarafrika.dto.PaymentConfirmationRequest;
import apps.sarafrika.entity.Order;
import apps.sarafrika.entity.Payment;
import apps.sarafrika.enums.PaymentStatus;
import apps.sarafrika.service.OrderService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Path("/api/payments")
@Tag(name = "Payment", description = "Payment confirmation operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentController {

    private static final Logger LOG = Logger.getLogger(PaymentController.class);

    @Inject
    OrderService orderService;

    @POST
    @Path("/confirm")
    @Transactional
    @Operation(
        summary = "Confirm a payment",
        description = "Receives and processes payment confirmation details from payment providers. " +
                     "Uses the bill_reference (order number) to match outstanding orders and logs the payment information."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Payment confirmation processed successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid request data or order not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "409",
            description = "Order already paid or payment amount mismatch",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public Response confirmPayment(
        @RequestBody(
            description = "Payment confirmation details with bill_reference as order number",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = PaymentConfirmationRequest.class)
            )
        )
        @Valid PaymentConfirmationRequest request
    ) {
        LOG.infof("Received payment confirmation for order: %s, receipt: %s, amount: %s",
                 request.billReference(), request.receiptNo(), request.amountReceived());

        try {
            // 1. Check for duplicate payment using receiptNo
            Payment existingPayment = Payment.findByPaymentReference(request.receiptNo()).firstResult();
            if (existingPayment != null) {
                LOG.infof("Duplicate payment notification received for receipt: %s", request.receiptNo());
                return Response.ok(ApiResponse.success(
                    String.format("Payment with receipt %s already processed", request.receiptNo())
                )).build();
            }

            // 2. Find the order by reference code (bill_reference)
            Order order = orderService.findByReferenceCode(request.billReference());
            if (order == null) {
                LOG.warnf("Order not found for reference code: %s", request.billReference());
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity(ApiResponse.error("Order not found",
                                     "No order found with reference code: " + request.billReference()))
                              .build();
            }

            // 3. Check if order is already paid
            if (order.isPaid()) {
                LOG.warnf("Order %s is already paid", request.billReference());
                return Response.status(Response.Status.CONFLICT)
                              .entity(ApiResponse.error("Order already paid",
                                     "Order " + request.billReference() + " has already been paid"))
                              .build();
            }

            // 4. Validate payment amount matches order amount
            BigDecimal receivedAmount = BigDecimal.valueOf(request.amountReceived());
            if (order.orderAmount.compareTo(receivedAmount) != 0) {
                LOG.warnf("Payment amount mismatch for order %s. Expected: %s, Received: %s",
                         request.billReference(), order.orderAmount, receivedAmount);
                return Response.status(Response.Status.CONFLICT)
                              .entity(ApiResponse.error("Amount mismatch",
                                     String.format("Expected: %s, Received: %s", order.orderAmount, receivedAmount)))
                              .build();
            }

            // 5. Create payment record
            Payment payment = new Payment();
            payment.orderUuid = order.uuid;
            payment.paymentReference = request.receiptNo();
            payment.paymentAmount = receivedAmount;
            payment.paymentStatus = PaymentStatus.SUCCESS;
            payment.externalTransactionId = request.receiptNo();
            payment.paymentDate = LocalDateTime.now();
            payment.callbackReceivedDate = LocalDateTime.now();
            payment.createdBy = "PAYMENT_WEBHOOK";
            payment.persist();

            // 6. Update order status to paid and send SMS notification
            orderService.markAsPaidWithNotification(order, receivedAmount, "PAYMENT_WEBHOOK");

            LOG.infof("Successfully processed payment for order %s with receipt %s",
                     request.billReference(), request.receiptNo());

            ApiResponse<String> response = ApiResponse.success(
                String.format("Payment confirmed successfully for order %s", request.billReference())
            );
            return Response.ok(response).build();

        } catch (Exception e) {
            LOG.errorf(e, "Failed to process payment confirmation for order: %s", request.billReference());
            ApiResponse<String> errorResponse = ApiResponse.error(
                "Failed to process payment confirmation",
                e.getMessage()
            );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(errorResponse)
                          .build();
        }
    }
}