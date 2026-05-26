package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.*;
import com.Tejas.TaskManagementSystem.Exception.ErrorResponse;
import com.Tejas.TaskManagementSystem.Service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles workspace management operations including
 * workspace creation, user allocation and task tracking.
 * APIs are secured using JWT authentication and RBAC.
 */
@RestController
@RequestMapping(path = "/workspace")
@RequiredArgsConstructor
@Tag(
        name = "Workspace Controller",
        description = "APIs for workspace management, user allocation and task management"
)
public class WorkspaceController {
    private final WorkspaceService service;

    @Operation(
            summary = "Create workspace",
            description = "Creates a new workspace. Accessible only to ADMIN and MANAGER users.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/post")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody WorkspaceRequest request){
        WorkspaceResponse response = service.createWorkspace(request);
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Add users to workspace",
            description = "Adds multiple users to an existing workspace using workspace id.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users added successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized operation",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Workspace not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/post/users/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> addUsersToWorkspace(@PathVariable Long id,@Valid @RequestBody List<Long>ids){
        return new ResponseEntity<>(service.addUserToWorkspace(id,ids),HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch all workspaces",
            description = "Returns paginated list of all workspaces.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspaces fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    @GetMapping("/all")
    public ResponseEntity<List<WorkspaceResponse>> getAllWorkspace(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return new ResponseEntity<>(service.getAllWorkspace(page, size),HttpStatus.OK);
    }

    @Operation(
            summary = "Get workspace by id",
            description = "Fetch workspace details using workspace id.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Workspace not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/id/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(@PathVariable Long id){
        return new ResponseEntity<>(service.getWorkspaceById(id),HttpStatus.OK);
    }

    @Operation(
            summary = "Get workspace by name",
            description = "Fetch workspace details using workspace name.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Workspace not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/name/{name}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> getWorkspaceByName(@PathVariable String name){
        return new ResponseEntity<>(service.getWorkspaceByName(name),HttpStatus.OK);
    }

    @Operation(
            summary = "Update workspace",
            description = "Allows workspace creator to update workspace details.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace updated successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized operation",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Workspace not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/update/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> updateWorkspace(@PathVariable Long id,@Valid @RequestBody WorkspaceUpdateRequest request){
        return new ResponseEntity<>(service.updateWorkspace(id,request),HttpStatus.OK);
    }

    @Operation(
            summary = "Delete workspace",
            description = "Deletes workspace using workspace id.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized operation",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Workspace not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<String> deleteWorkspace(@PathVariable Long id){
        return new ResponseEntity<>(service.deleteWorkspace(id),HttpStatus.OK);
    }

    @Operation(
            summary = "Get all users of workspace",
            description = "Fetches all allocated users of a workspace.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Workspace not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/users/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<List<UserResponse>> getAllUsersForWorkspace(@PathVariable Long id){
        return new ResponseEntity<>(service.getAllUserOfWorkspace(id),HttpStatus.OK);
    }

    @Operation(
            summary = "Get all tasks of workspace",
            description = "Fetches all tasks associated with a workspace.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Workspace not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/task/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<List<TaskResponse>> getAllTasksForWorkspace(@PathVariable Long id){
        return new ResponseEntity<>(service.getAllTaskOfWorkspace(id),HttpStatus.OK);
    }

}
