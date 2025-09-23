package apps.sarafrika.controller;

import apps.sarafrika.dto.CelcomDlrRequest;
import apps.sarafrika.service.SmsTrackingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/webhooks/sms")
public class SmsWebhookController {

    @Inject
    SmsTrackingService smsTrackingService;

    @POST
    @Path("/celcom/dlr")
    public Response receiveDlr(CelcomDlrRequest dlrRequest) {
        if (dlrRequest == null || dlrRequest.messageId() == null || dlrRequest.deliveryStatus() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        smsTrackingService.updateSmsStatus(dlrRequest.messageId(), dlrRequest.deliveryStatus(), dlrRequest.deliveryStatus());

        return Response.ok().build();
    }
}
