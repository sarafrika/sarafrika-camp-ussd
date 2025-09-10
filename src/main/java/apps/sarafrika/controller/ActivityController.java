package apps.sarafrika.controller;

import apps.sarafrika.dto.ActivityDto;
import apps.sarafrika.dto.ActivityFactory;
import apps.sarafrika.dto.ApiResponse;
import apps.sarafrika.entity.Activity;
import apps.sarafrika.service.ActivityService;
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

@Path("/api/activities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Activity Management", description = "Dynamic activity management API")
public class ActivityController {

    @Inject
    ActivityService activityService;

    @Inject
    ActivityFactory activityFactory;

    @GET
    @Operation(
        summary = "List all active activities",
        description = "Retrieve all active activities with optional filtering"
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
                                "uuid": "550e8400-e29b-41d4-a716-446655440000",
                                "name": "Photography Workshop",
                                "camp_uuid": "550e8400-e29b-41d4-a716-446655440001",
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
                        "error": "Invalid camp UUID format"
                    }
                    """
                )
            )
        )
    })
    public Response listActivities(
            @Parameter(description = "Filter by camp UUID")
            @QueryParam("campUuid") String campUuid) {
        
        try {
            List<Activity> activities;
            
            if (campUuid != null && !campUuid.trim().isEmpty()) {
                UUID campUuidParsed = UUID.fromString(campUuid);
                activities = activityService.getActivitiesByCamp(campUuidParsed);
            } else {
                activities = activityService.findAllActive();
            }
            
            List<ActivityDto> activityDtos = activityFactory.fromEntityList(activities);
            return Response.ok(ApiResponse.success(activityDtos)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Invalid camp UUID format"))
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(ApiResponse.error("Failed to retrieve activities"))
                         .build();
        }
    }

    @GET
    @Path("/{uuid}")
    @Operation(
        summary = "Get activity by UUID",
        description = "Retrieve a specific activity by its UUID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Activity retrieved successfully",
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
                            "name": "Photography Workshop",
                            "camp_uuid": "550e8400-e29b-41d4-a716-446655440001",
                            "is_available": true,
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
            description = "Activity not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Activity not found"
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
    public Response getActivity(
            @Parameter(description = "Activity UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID activityUuid = UUID.fromString(uuid);
            Activity activity = activityService.findByUuid(activityUuid);
            
            if (activity == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(ApiResponse.error("Activity not found"))
                             .build();
            }
            
            ActivityDto activityDto = activityFactory.fromEntity(activity);
            return Response.ok(ApiResponse.success(activityDto)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Invalid UUID format"))
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(ApiResponse.error("Failed to retrieve activity"))
                         .build();
        }
    }

    @POST
    @Operation(
        summary = "Create new activity",
        description = "Create a new activity with all required details"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201", 
            description = "Activity created successfully",
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
                            "name": "Photography Workshop",
                            "camp_uuid": "550e8400-e29b-41d4-a716-446655440001",
                            "is_available": true,
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
            description = "Invalid activity data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Failed to create activity",
                        "details": "Activity name is required"
                    }
                    """
                )
            )
        )
    })
    public Response createActivity(
        @RequestBody(
            description = "Activity data to create",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ActivityDto.class),
                examples = @ExampleObject(
                    name = "Create Activity Request",
                    value = """
                    {
                        "name": "Photography Workshop",
                        "camp_uuid": "550e8400-e29b-41d4-a716-446655440001",
                        "is_available": true
                    }
                    """
                )
            )
        )
        @Valid ActivityDto activityDto) {
        try {
            Activity activity = activityFactory.toEntity(activityDto);
            activity.createdBy = "api";
            Activity createdActivity = activityService.createActivity(activity);
            
            ActivityDto responseDto = activityFactory.fromEntity(createdActivity);
            return Response.status(Response.Status.CREATED)
                         .entity(ApiResponse.success(responseDto))
                         .build();
                         
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Failed to create activity", e.getMessage()))
                         .build();
        }
    }

    @PUT
    @Path("/{uuid}")
    @Operation(
        summary = "Update existing activity",
        description = "Update an existing activity by its UUID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Activity updated successfully",
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
                            "name": "Advanced Photography Workshop",
                            "camp_uuid": "550e8400-e29b-41d4-a716-446655440001",
                            "is_available": true,
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
            description = "Activity not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Activity not found"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "400", 
            description = "Invalid activity data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "success": false,
                        "error": "Failed to update activity",
                        "details": "Invalid UUID format"
                    }
                    """
                )
            )
        )
    })
    public Response updateActivity(
            @Parameter(description = "Activity UUID", required = true)
            @PathParam("uuid") String uuid,
            @RequestBody(
                description = "Updated activity data",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ActivityDto.class),
                    examples = @ExampleObject(
                        name = "Update Activity Request",
                        value = """
                        {
                            "name": "Advanced Photography Workshop",
                            "camp_uuid": "550e8400-e29b-41d4-a716-446655440001",
                            "is_available": true
                        }
                        """
                    )
                )
            )
            @Valid ActivityDto activityDto) {
        
        try {
            UUID activityUuid = UUID.fromString(uuid);
            Activity existingActivity = activityService.findByUuid(activityUuid);
            
            if (existingActivity == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(ApiResponse.error("Activity not found"))
                             .build();
            }
            
            activityFactory.updateEntity(existingActivity, activityDto);
            existingActivity.updatedBy = "api";
            Activity updatedActivity = activityService.updateActivity(existingActivity);
            
            ActivityDto responseDto = activityFactory.fromEntity(updatedActivity);
            return Response.ok(ApiResponse.success(responseDto)).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Invalid UUID format"))
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Failed to update activity", e.getMessage()))
                         .build();
        }
    }

    @DELETE
    @Path("/{uuid}")
    @Operation(
        summary = "Delete activity",
        description = "Soft delete an activity by its UUID"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200", 
            description = "Activity deleted successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "success": true,
                        "data": "Activity deleted successfully"
                    }
                    """
                )
            )
        ),
        @APIResponse(
            responseCode = "404", 
            description = "Activity not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                        "success": false,
                        "error": "Activity not found"
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
    public Response deleteActivity(
            @Parameter(description = "Activity UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID activityUuid = UUID.fromString(uuid);
            Activity activity = activityService.findByUuid(activityUuid);
            
            if (activity == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(ApiResponse.error("Activity not found"))
                             .build();
            }
            
            activityService.deleteActivity(activityUuid, "api");
            return Response.ok(ApiResponse.success("Activity deleted successfully")).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(ApiResponse.error("Invalid UUID format"))
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(ApiResponse.error("Failed to delete activity"))
                         .build();
        }
    }

}