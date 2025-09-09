package apps.sarafrika.controller;

import apps.sarafrika.entity.Location;
import apps.sarafrika.service.LocationService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

@Path("/api/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Locations", description = "Location management API for Camp Sarafrika")
public class LocationController {

    private static final Logger LOG = Logger.getLogger(LocationController.class);

    @Inject
    LocationService locationService;

    @GET
    @Operation(
        summary = "Get all active locations",
        description = "Retrieve all active locations with their associated fees"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved locations",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Location.class)
            )
        )
    })
    public Response getAllLocations() {
        try {
            List<Location> locations = locationService.findAllActive();
            return Response.ok(locations).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error retrieving locations");
            return Response.serverError()
                .entity("{\"error\":\"Failed to retrieve locations\"}")
                .build();
        }
    }

    @GET
    @Path("/{uuid}")
    @Operation(
        summary = "Get location by UUID",
        description = "Retrieve a specific location by its UUID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Location found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Location.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Location not found"
        )
    })
    public Response getLocationByUuid(
        @Parameter(description = "Location UUID", required = true)
        @PathParam("uuid") String uuidStr
    ) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Location location = locationService.findByUuid(uuid);
            
            if (location == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Location not found\"}")
                    .build();
            }
            
            return Response.ok(location).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Invalid UUID format\"}")
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error retrieving location with UUID: %s", uuidStr);
            return Response.serverError()
                .entity("{\"error\":\"Failed to retrieve location\"}")
                .build();
        }
    }

    @POST
    @Operation(
        summary = "Create new location",
        description = "Create a new location with name and fee"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Location created successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Location.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    public Response createLocation(@Valid Location location) {
        try {
            if (location.name == null || location.name.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Location name is required\"}")
                    .build();
            }
            
            if (location.fee == null || location.fee.doubleValue() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Location fee must be greater than 0\"}")
                    .build();
            }
            
            location.createdBy = "api";
            Location created = locationService.createLocation(location);
            
            return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error creating location: %s", location != null ? location.name : "null");
            return Response.serverError()
                .entity("{\"error\":\"Failed to create location\"}")
                .build();
        }
    }

    @PUT
    @Path("/{uuid}")
    @Operation(
        summary = "Update location",
        description = "Update an existing location's details"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Location updated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Location.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Location not found"
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    public Response updateLocation(
        @Parameter(description = "Location UUID", required = true)
        @PathParam("uuid") String uuidStr,
        @Valid Location locationUpdate
    ) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Location existing = locationService.findByUuid(uuid);
            
            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Location not found\"}")
                    .build();
            }
            
            if (locationUpdate.name != null && !locationUpdate.name.trim().isEmpty()) {
                existing.name = locationUpdate.name;
            }
            
            if (locationUpdate.fee != null && locationUpdate.fee.doubleValue() > 0) {
                existing.fee = locationUpdate.fee;
            }
            
            existing.updatedBy = "api";
            Location updated = locationService.updateLocation(existing);
            
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Invalid UUID format\"}")
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error updating location with UUID: %s", uuidStr);
            return Response.serverError()
                .entity("{\"error\":\"Failed to update location\"}")
                .build();
        }
    }

    @DELETE
    @Path("/{uuid}")
    @Operation(
        summary = "Delete location",
        description = "Soft delete a location (marks as deleted)"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "Location deleted successfully"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Location not found"
        )
    })
    public Response deleteLocation(
        @Parameter(description = "Location UUID", required = true)
        @PathParam("uuid") String uuidStr
    ) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Location existing = locationService.findByUuid(uuid);
            
            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Location not found\"}")
                    .build();
            }
            
            locationService.deleteLocation(uuid, "api");
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Invalid UUID format\"}")
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error deleting location with UUID: %s", uuidStr);
            return Response.serverError()
                .entity("{\"error\":\"Failed to delete location\"}")
                .build();
        }
    }
}