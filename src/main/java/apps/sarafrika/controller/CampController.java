package apps.sarafrika.controller;

import apps.sarafrika.dto.ActivityDto;
import apps.sarafrika.dto.ActivityFactory;
import apps.sarafrika.dto.ApiResponse;
import apps.sarafrika.dto.CampDto;
import apps.sarafrika.dto.CampFactory;
import apps.sarafrika.entity.Activity;
import apps.sarafrika.entity.Camp;
import apps.sarafrika.service.ActivityService;
import apps.sarafrika.service.CampService;
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

import java.util.List;
import java.util.UUID;

@Path("/api/camps")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Camp Management", description = "Dynamic camp and activity management API")
public class CampController {

    @Inject
    CampService campService;

    @Inject
    ActivityService activityService;

    @Inject
    CampFactory campFactory;

    @Inject
    ActivityFactory activityFactory;

    @GET
    @Operation(
        summary = "List all active camps",
        description = "Retrieve all active camps with optional category filtering"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Camps retrieved successfully",
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
                                "name": "Summer Photography Camp",
                                "camp_type": "HALF_DAY",
                                "location_uuids": ["550e8400-e29b-41d4-a716-446655440001"],
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
        )
    })
    public Response listCamps() {
        
        try {
            List<Camp> camps = campService.findAllActive();
            List<CampDto> campDtos = campFactory.fromEntityList(camps);
            return Response.ok(ApiResponse.success(campDtos)).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(ApiResponse.error("Failed to retrieve camps"))
                         .build();
        }
    }

    @GET
    @Path("/{uuid}")
    @Operation(
        summary = "Get camp by UUID",
        description = "Retrieve a specific camp by its UUID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Camp retrieved successfully",
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
                            "name": "Summer Photography Camp",
                            "camp_type": "HALF_DAY",
                            "location_uuids": ["550e8400-e29b-41d4-a716-446655440001"],
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
            description = "Camp not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Camp not found"
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
    public Response getCamp(
            @Parameter(description = "Camp UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID campUuid = UUID.fromString(uuid);
            Camp camp = campService.findByUuid(campUuid);
            
            if (camp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(ApiResponse.error("Camp not found"))
                             .build();
            }
            
            CampDto campDto = campFactory.fromEntity(camp);
            return Response.ok(ApiResponse.success(campDto)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Invalid UUID format"))
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(ApiResponse.error("Failed to retrieve camp"))
                         .build();
        }
    }

    @POST
    @Operation(
        summary = "Create new camp",
        description = "Create a new camp with all required details"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201", 
            description = "Camp created successfully",
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
                            "name": "Summer Photography Camp",
                            "camp_type": "HALF_DAY",
                            "location_uuids": ["550e8400-e29b-41d4-a716-446655440001"],
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
            description = "Invalid camp data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Failed to create camp",
                        "details": "Camp name is required"
                    }
                    """
                )
            )
        )
    })
    public Response createCamp(
        @RequestBody(
            description = "Camp data to create",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = CampDto.class),
                examples = @ExampleObject(
                    name = "Create Camp Request",
                    value = """
                    {
                        "name": "Summer Photography Camp",
                        "camp_type": "HALF_DAY",
                        "location_uuids": ["550e8400-e29b-41d4-a716-446655440001", "660e8400-e29b-41d4-a716-446655440002"]
                    }
                    """
                )
            )
        )
        @Valid CampDto campDto) {
        try {
            Camp camp = campFactory.toEntity(campDto);
            camp.createdBy = "api";
            
            if (campDto.locationUuids != null) {
                campFactory.setLocationsFromUuids(camp, campDto.locationUuids);
            }
            
            Camp createdCamp = campService.createCamp(camp);
            
            CampDto responseDto = campFactory.fromEntity(createdCamp);
            return Response.status(Response.Status.CREATED)
                         .entity(ApiResponse.success(responseDto))
                         .build();
                         
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Failed to create camp", e.getMessage()))
                         .build();
        }
    }

    @PUT
    @Path("/{uuid}")
    @Operation(
        summary = "Update existing camp",
        description = "Update an existing camp by its UUID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Camp updated successfully",
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
                            "name": "Advanced Photography Camp",
                            "camp_type": "BOOT_CAMP",
                            "location_uuids": ["550e8400-e29b-41d4-a716-446655440001"],
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
            description = "Camp not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Camp not found"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400", 
            description = "Invalid camp data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Failed to update camp",
                        "details": "Invalid camp type"
                    }
                    """
                )
            )
        )
    })
    public Response updateCamp(
            @Parameter(description = "Camp UUID", required = true)
            @PathParam("uuid") String uuid,
            @RequestBody(
                description = "Updated camp data",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CampDto.class),
                    examples = @ExampleObject(
                        name = "Update Camp Request",
                        value = """
                        {
                            "name": "Advanced Photography Camp",
                            "camp_type": "BOOT_CAMP",
                            "location_uuids": ["550e8400-e29b-41d4-a716-446655440001"]
                        }
                        """
                    )
                )
            )
            @Valid CampDto campDto) {
        
        try {
            UUID campUuid = UUID.fromString(uuid);
            Camp existingCamp = campService.findByUuid(campUuid);
            
            if (existingCamp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(ApiResponse.error("Camp not found"))
                             .build();
            }
            
            campFactory.updateEntity(existingCamp, campDto);
            existingCamp.updatedBy = "api";
            if (campDto.locationUuids != null) {
                campFactory.setLocationsFromUuids(existingCamp, campDto.locationUuids);
            }
            
            Camp updatedCamp = campService.updateCamp(existingCamp);
            
            CampDto responseDto = campFactory.fromEntity(updatedCamp);
            return Response.ok(ApiResponse.success(responseDto)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Invalid UUID format"))
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Failed to update camp", e.getMessage()))
                         .build();
        }
    }

    @DELETE
    @Path("/{uuid}")
    @Operation(
        summary = "Delete camp",
        description = "Soft delete a camp by its UUID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Camp deleted successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "data": "Camp deleted successfully"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "404", 
            description = "Camp not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Camp not found"
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
    public Response deleteCamp(
            @Parameter(description = "Camp UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID campUuid = UUID.fromString(uuid);
            Camp camp = campService.findByUuid(campUuid);
            
            if (camp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(ApiResponse.error("Camp not found"))
                             .build();
            }
            
            campService.deleteCamp(campUuid, "api");
            return Response.ok(ApiResponse.success("Camp deleted successfully")).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Invalid UUID format"))
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(ApiResponse.error("Failed to delete camp"))
                         .build();
        }
    }

    @GET
    @Path("/names")
    @Operation(
        summary = "Get distinct camp names",
        description = "Retrieve all distinct camp names"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Camp names retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "data": [
                            "Summer Photography Camp",
                            "Advanced Arts Workshop",
                            "Music Production Bootcamp"
                        ]
                    }
                    """
                )
            )
        )
    })
    public Response getCampNames() {
        try {
            List<String> names = campService.getDistinctCampNames();
            return Response.ok(ApiResponse.success(names)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(ApiResponse.error("Failed to retrieve camp names"))
                         .build();
        }
    }

    @GET
    @Path("/{uuid}/activities")
    @Operation(
        summary = "Get camp activities",
        description = "Retrieve all activities for a specific camp"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Activities retrieved successfully",
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
                                "uuid": "550e8400-e29b-41d4-a716-446655440002",
                                "name": "Photography Basics",
                                "camp_uuid": "550e8400-e29b-41d4-a716-446655440000",
                                "is_available": true,
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
            responseCode = "404", 
            description = "Camp not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Camp not found"
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
    public Response getCampActivities(
            @Parameter(description = "Camp UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID campUuid = UUID.fromString(uuid);
            Camp camp = campService.findByUuid(campUuid);
            
            if (camp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(ApiResponse.error("Camp not found"))
                             .build();
            }
            
            List<Activity> activities = activityService.getActivitiesByCamp(campUuid);
            List<ActivityDto> activityDtos = activityFactory.fromEntityList(activities);
            return Response.ok(ApiResponse.success(activityDtos)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Invalid UUID format"))
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(ApiResponse.error("Failed to retrieve camp activities"))
                         .build();
        }
    }
}