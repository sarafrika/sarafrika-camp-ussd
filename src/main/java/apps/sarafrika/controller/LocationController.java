package apps.sarafrika.controller;

import apps.sarafrika.dto.ApiResponse;
import apps.sarafrika.dto.LocationDto;
import apps.sarafrika.dto.LocationFactory;
import apps.sarafrika.entity.Location;
import apps.sarafrika.service.LocationService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
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

    @Inject
    LocationFactory locationFactory;

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
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "data": [
                            {
                                "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                "name": "Nairobi",
                                "fee": 5000.00,
                                "dates": "2024-12-01,2024-12-15",
                                "created_date": "2024-09-10T10:00:00",
                                "created_by": "api",
                                "updated_date": "2024-09-10T10:00:00",
                                "updated_by": "api"
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Failed to retrieve locations"
                    }
                    """
                )
            )
        )
    })
    public Response getAllLocations() {
        try {
            List<Location> locations = locationService.findAllActive();
            List<LocationDto> locationDtos = locationFactory.fromEntityList(locations);
            return Response.ok(ApiResponse.success(locationDtos)).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error retrieving locations");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error("Failed to retrieve locations"))
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
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "data": {
                            "uuid": "550e8400-e29b-41d4-a716-446655440000",
                            "name": "Nairobi",
                            "fee": 5000.00,
                            "dates": "2024-12-01,2024-12-15",
                            "created_date": "2024-09-10T10:00:00",
                            "created_by": "api",
                            "updated_date": "2024-09-10T10:00:00",
                            "updated_by": "api"
                        }
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Location not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Location not found"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Invalid UUID format"
                    }
                    """
                )
            )
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
                    .entity(ApiResponse.error("Location not found"))
                    .build();
            }
            
            LocationDto locationDto = locationFactory.fromEntity(location);
            return Response.ok(ApiResponse.success(locationDto)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Invalid UUID format"))
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error retrieving location with UUID: %s", uuidStr);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error("Failed to retrieve location"))
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
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "data": {
                            "uuid": "550e8400-e29b-41d4-a716-446655440000",
                            "name": "Nairobi",
                            "fee": 5000.00,
                            "dates": "2024-12-01,2024-12-15",
                            "created_date": "2024-09-10T10:00:00",
                            "created_by": "api",
                            "updated_date": "2024-09-10T10:00:00",
                            "updated_by": "api"
                        }
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Failed to create location",
                        "details": "Location name is required"
                    }
                    """
                )
            )
        )
    })
    public Response createLocation(
        @RequestBody(
            description = "Location data to create",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = LocationDto.class),
                examples = @ExampleObject(
                    name = "Create Location Request",
                    value = """
                    {
                        "name": "Nairobi",
                        "fee": 5000.00,
                        "dates": "2024-12-01,2024-12-15"
                    }
                    """
                )
            )
        )
        @Valid LocationDto locationDto) {
        try {
            Location location = locationFactory.toEntity(locationDto);
            location.createdBy = "api";
            Location created = locationService.createLocation(location);
            
            LocationDto responseDto = locationFactory.fromEntity(created);
            return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(responseDto))
                .build();
                
        } catch (Exception e) {
            LOG.errorf(e, "Error creating location: %s", locationDto != null ? locationDto.name : "null");
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Failed to create location", e.getMessage()))
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
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "data": {
                            "uuid": "550e8400-e29b-41d4-a716-446655440000",
                            "name": "Nairobi Central",
                            "fee": 6000.00,
                            "dates": "2024-12-01,2024-12-15,2024-12-30",
                            "created_date": "2024-09-10T10:00:00",
                            "created_by": "api",
                            "updated_date": "2024-09-10T11:00:00",
                            "updated_by": "api"
                        }
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Location not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Location not found"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Failed to update location",
                        "details": "Invalid UUID format"
                    }
                    """
                )
            )
        )
    })
    public Response updateLocation(
        @Parameter(description = "Location UUID", required = true)
        @PathParam("uuid") String uuidStr,
        @RequestBody(
            description = "Updated location data",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = LocationDto.class),
                examples = @ExampleObject(
                    name = "Update Location Request",
                    value = """
                    {
                        "name": "Nairobi Central",
                        "fee": 6000.00,
                        "dates": "2024-12-01,2024-12-15,2024-12-30"
                    }
                    """
                )
            )
        )
        @Valid LocationDto locationDto
    ) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Location existing = locationService.findByUuid(uuid);
            
            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("Location not found"))
                    .build();
            }
            
            locationFactory.updateEntity(existing, locationDto);
            existing.updatedBy = "api";
            Location updated = locationService.updateLocation(existing);
            
            LocationDto responseDto = locationFactory.fromEntity(updated);
            return Response.ok(ApiResponse.success(responseDto)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Invalid UUID format"))
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error updating location with UUID: %s", uuidStr);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error("Failed to update location", e.getMessage()))
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
            responseCode = "200",
            description = "Location deleted successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "data": "Location deleted successfully"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Location not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Location not found"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Invalid UUID format"
                    }
                    """
                )
            )
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
                    .entity(ApiResponse.error("Location not found"))
                    .build();
            }
            
            locationService.deleteLocation(uuid, "api");
            return Response.ok(ApiResponse.success("Location deleted successfully")).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error("Invalid UUID format"))
                .build();
        } catch (Exception e) {
            LOG.errorf(e, "Error deleting location with UUID: %s", uuidStr);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error("Failed to delete location", e.getMessage()))
                .build();
        }
    }
}