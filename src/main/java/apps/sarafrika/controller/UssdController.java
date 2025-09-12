package apps.sarafrika.controller;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.service.SessionService;
import apps.sarafrika.service.TrackingService;
import apps.sarafrika.service.UssdMenuService;
import apps.sarafrika.enums.InteractionType;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/ussd")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_PLAIN)
@Tag(name = "USSD", description = "Camp Sarafrika USSD Service API")
public class UssdController {

    private static final Logger LOG = Logger.getLogger(UssdController.class);

    @Inject
    SessionService sessionService;

    @Inject
    UssdMenuService ussdMenuService;

    @Inject
    TrackingService trackingService;

    @POST
    @Operation(
        summary = "Handle USSD Request",
        description = "Main webhook endpoint for processing USSD requests from Africa's Talking. " +
                     "Manages camp registration flow with session-based state management."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "USSD response successfully generated",
            content = @Content(
                mediaType = MediaType.TEXT_PLAIN,
                examples = {
                    @ExampleObject(
                        name = "Welcome Menu",
                        value = "CON Welcome to Camp Sarafrika!\n\n1. Register for a Camp\n2. My Bookings\n3. Help\n4. Exit"
                    ),
                    @ExampleObject(
                        name = "Registration Complete",
                        value = "END Registration successful!\n\nReference: CS-A4T9B1C7\n\nPlease complete payment via M-Pesa.\nYou will receive an SMS confirmation.\n\nThank you for choosing Camp Sarafrika!"
                    )
                }
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.TEXT_PLAIN,
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "END Sorry, we're experiencing technical difficulties. Please try again later."
                )
            )
        )
    })
    public Response handleUssdRequest(
            @Parameter(
                description = "Unique session identifier from Africa's Talking",
                required = true,
                example = "ATUid_abcd1234567890"
            )
            @FormParam("sessionId") String sessionId,
            
            @Parameter(
                description = "User's phone number in international format",
                required = true,
                example = "+254712345678"
            )
            @FormParam("phoneNumber") String phoneNumber,
            
            @Parameter(
                description = "Mobile network operator code",
                required = true,
                example = "63902"
            )
            @FormParam("networkCode") String networkCode,
            
            @Parameter(
                description = "USSD service code that was dialed",
                required = true,
                example = "*789*2020#"
            )
            @FormParam("serviceCode") String serviceCode,
            
            @Parameter(
                description = "User input text. Empty for initial request, contains asterisk-separated values for subsequent requests",
                required = false,
                examples = {
                    @ExampleObject(name = "Initial Request", value = ""),
                    @ExampleObject(name = "Select Option 1", value = "1"),
                    @ExampleObject(name = "Navigate and Select", value = "1*2*John Doe*16")
                }
            )
            @FormParam("text") String text
    ) {
        long startTime = System.currentTimeMillis();
        String previousState = null;
        String currentState = null;
        
        try {
            LOG.infof("USSD Request - SessionId: %s, Phone: %s, Text: '%s'", 
                     sessionId, phoneNumber, text);

            UserSession session = sessionService.getSession(sessionId)
                    .orElse(new UserSession(sessionId, phoneNumber));

            previousState = session.getCurrentState();
            
            String response = ussdMenuService.processUssdInput(session, text);
            
            currentState = session.getCurrentState();
            
            sessionService.saveSession(sessionId, session);

            LOG.infof("USSD Response - SessionId: %s, Response: '%s'", sessionId, response);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            trackingService.trackInteractionAsync(sessionId, phoneNumber, InteractionType.INPUT, 
                    currentState, previousState, text, response, (int) processingTime, null, null);
            
            return Response.ok(response).build();

        } catch (Exception e) {
            LOG.errorf(e, "Error processing USSD request for session %s", sessionId);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            trackingService.trackInteractionAsync(sessionId, phoneNumber, InteractionType.VALIDATION_ERROR, 
                    currentState, previousState, text, null, (int) processingTime, e.getMessage(), null);
            
            return Response.ok("END Sorry, we're experiencing technical difficulties. Please try again later.").build();
        }
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Health")
    @Operation(
        summary = "Health Check",
        description = "Returns the health status of the USSD service. Used for monitoring and load balancer health checks."
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Service is healthy and operational",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                examples = @ExampleObject(
                    name = "Healthy Status",
                    value = "{\"status\":\"UP\",\"service\":\"sarafrika-camp-ussd\"}"
                )
            )
        )
    })
    public Response health() {
        return Response.ok("{\"status\":\"UP\",\"service\":\"sarafrika-camp-ussd\"}").build();
    }
}