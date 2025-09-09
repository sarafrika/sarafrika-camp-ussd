package apps.sarafrika.controller;

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
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
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

    @GET
    @Operation(
        summary = "List all active camps",
        description = "Retrieve all active camps with optional category filtering"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Camps retrieved successfully")
    })
    public Response listCamps(
            @Parameter(description = "Filter by camp category")
            @QueryParam("category") String category) {
        
        List<Camp> camps;
        if (category != null && !category.trim().isEmpty()) {
            camps = campService.getCampsByCategory(category, 0, Integer.MAX_VALUE);
        } else {
            camps = campService.findAllActive();
        }
        
        return Response.ok(camps).build();
    }

    @GET
    @Path("/{uuid}")
    @Operation(
        summary = "Get camp by UUID",
        description = "Retrieve a specific camp by its UUID"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Camp retrieved successfully"),
        @APIResponse(responseCode = "404", description = "Camp not found")
    })
    public Response getCamp(
            @Parameter(description = "Camp UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID campUuid = UUID.fromString(uuid);
            Camp camp = campService.findByUuid(campUuid);
            
            if (camp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity("{\"error\":\"Camp not found\"}")
                             .build();
            }
            
            return Response.ok(camp).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Invalid UUID format\"}")
                         .build();
        }
    }

    @POST
    @Operation(
        summary = "Create new camp",
        description = "Create a new camp with all required details"
    )
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Camp created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid camp data")
    })
    public Response createCamp(@Valid Camp camp) {
        try {
            Camp createdCamp = campService.createCamp(camp);
            return Response.status(Response.Status.CREATED).entity(createdCamp).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Failed to create camp: " + e.getMessage() + "\"}")
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
        @APIResponse(responseCode = "200", description = "Camp updated successfully"),
        @APIResponse(responseCode = "404", description = "Camp not found"),
        @APIResponse(responseCode = "400", description = "Invalid camp data")
    })
    public Response updateCamp(
            @Parameter(description = "Camp UUID", required = true)
            @PathParam("uuid") String uuid, 
            @Valid Camp camp) {
        
        try {
            UUID campUuid = UUID.fromString(uuid);
            Camp existingCamp = campService.findByUuid(campUuid);
            
            if (existingCamp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity("{\"error\":\"Camp not found\"}")
                             .build();
            }
            
            camp.uuid = campUuid;
            Camp updatedCamp = campService.updateCamp(camp);
            return Response.ok(updatedCamp).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Invalid UUID format\"}")
                         .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Failed to update camp: " + e.getMessage() + "\"}")
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
        @APIResponse(responseCode = "204", description = "Camp deleted successfully"),
        @APIResponse(responseCode = "404", description = "Camp not found")
    })
    public Response deleteCamp(
            @Parameter(description = "Camp UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID campUuid = UUID.fromString(uuid);
            Camp camp = campService.findByUuid(campUuid);
            
            if (camp == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            campService.deleteCamp(campUuid, "system");
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
        summary = "Get distinct categories",
        description = "Retrieve all distinct camp categories"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    public Response getCategories() {
        List<String> categories = campService.getDistinctCategories();
        return Response.ok(categories).build();
    }

    @GET
    @Path("/{uuid}/activities")
    @Operation(
        summary = "Get camp activities",
        description = "Retrieve all activities for a specific camp"
    )
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Activities retrieved successfully"),
        @APIResponse(responseCode = "404", description = "Camp not found")
    })
    public Response getCampActivities(
            @Parameter(description = "Camp UUID", required = true)
            @PathParam("uuid") String uuid) {
        
        try {
            UUID campUuid = UUID.fromString(uuid);
            Camp camp = campService.findByUuid(campUuid);
            
            if (camp == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity("{\"error\":\"Camp not found\"}")
                             .build();
            }
            
            List<Activity> activities = activityService.getActivitiesByCamp(campUuid);
            return Response.ok(activities).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity("{\"error\":\"Invalid UUID format\"}")
                         .build();
        }
    }
}