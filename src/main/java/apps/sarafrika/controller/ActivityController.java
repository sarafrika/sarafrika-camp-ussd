package apps.sarafrika.controller;

import apps.sarafrika.entity.Activity;
import apps.sarafrika.service.ActivityService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
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

    @GET
    @Operation(
        summary = "List all active activities",
        description = "Retrieve all active activities with optional filtering"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Activities retrieved successfully")
    })
    public Response listActivities(
            @Parameter(description = "Filter by camp UUID")
            @QueryParam("campUuid") String campUuid,
            @Parameter(description = "Filter by activity category")
            @QueryParam("category") String category) {
        
        List<Activity> activities;
        
        if (campUuid != null && !campUuid.trim().isEmpty()) {
            try {
                UUID campUuidParsed = UUID.fromString(campUuid);
                activities = activityService.getActivitiesByCamp(campUuidParsed);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity("{\"error\":\"Invalid camp UUID format\"}")
                             .build();
            }
        } else if (category != null && !category.trim().isEmpty()) {
            activities = activityService.getActivitiesByCategory(category);
        } else {
            activities = activityService.findAllActive();
        }
        
        return Response.ok(activities).build();
    }

    @GET
    @Path("/{uuid}")
    @Operation(
        summary = "Get activity by UUID",
        description = "Retrieve a specific activity by its UUID"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Activity retrieved successfully"),
        @APIResponse(responseCode = "404", description = "Activity not found")
    })
    public Response getActivity(
            @Parameter(description = "Activity UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID activityUuid = UUID.fromString(uuid);
            Activity activity = activityService.findByUuid(activityUuid);
            
            if (activity == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity("{\"error\":\"Activity not found\"}")
                             .build();
            }
            
            return Response.ok(activity).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Invalid UUID format\"}")
                         .build();
        }
    }

    @POST
    @Operation(
        summary = "Create new activity",
        description = "Create a new activity with all required details"
    )
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Activity created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid activity data")
    })
    public Response createActivity(@Valid Activity activity) {
        try {
            Activity createdActivity = activityService.createActivity(activity);
            return Response.status(Response.Status.CREATED).entity(createdActivity).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Failed to create activity: " + e.getMessage() + "\"}")
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
        @APIResponse(responseCode = "200", description = "Activity updated successfully"),
        @APIResponse(responseCode = "404", description = "Activity not found"),
        @APIResponse(responseCode = "400", description = "Invalid activity data")
    })
    public Response updateActivity(
            @Parameter(description = "Activity UUID", required = true)
            @PathParam("uuid") String uuid, 
            @Valid Activity activity) {
        
        try {
            UUID activityUuid = UUID.fromString(uuid);
            Activity existingActivity = activityService.findByUuid(activityUuid);
            
            if (existingActivity == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity("{\"error\":\"Activity not found\"}")
                             .build();
            }
            
            activity.uuid = activityUuid;
            Activity updatedActivity = activityService.updateActivity(activity);
            return Response.ok(updatedActivity).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Invalid UUID format\"}")
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Failed to update activity: " + e.getMessage() + "\"}")
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
        @APIResponse(responseCode = "204", description = "Activity deleted successfully"),
        @APIResponse(responseCode = "404", description = "Activity not found")
    })
    public Response deleteActivity(
            @Parameter(description = "Activity UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID activityUuid = UUID.fromString(uuid);
            Activity activity = activityService.findByUuid(activityUuid);
            
            if (activity == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            activityService.deleteActivity(activityUuid, "system");
            return Response.noContent().build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Invalid UUID format\"}")
                         .build();
        }
    }

    @GET
    @Path("/categories")
    @Operation(
        summary = "Get distinct activity categories",
        description = "Retrieve all distinct activity categories"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    public Response getCategories() {
        List<String> categories = activityService.getDistinctCategories();
        return Response.ok(categories).build();
    }
}