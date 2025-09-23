package apps.sarafrika.service;

import apps.sarafrika.dto.CelcomSmsRequest;
import apps.sarafrika.dto.CelcomSmsResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/services")
@RegisterRestClient(configKey = "celcom-africa")
public interface CelcomAfricaClient {

    @POST
    @Path("/sendsms")
    @Produces(MediaType.APPLICATION_JSON)
    CelcomSmsResponse sendSms(CelcomSmsRequest request);
}
