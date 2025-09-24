package apps.sarafrika.service;

import apps.sarafrika.dto.StkPushRequest;
import apps.sarafrika.dto.StkPushResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/mobile")
@RegisterRestClient(configKey = "cheddar-api")
public interface CheddarApi {

    @POST
    @Path("/request-payment")
    @Produces(MediaType.APPLICATION_JSON)
    StkPushResponse requestPayment(StkPushRequest request);
}
