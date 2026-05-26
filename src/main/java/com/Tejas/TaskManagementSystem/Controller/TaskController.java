package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.TaskRequest;
import com.Tejas.TaskManagementSystem.DTO.TaskResponse;
import com.Tejas.TaskManagementSystem.DTO.TaskUpdateRequest;
import com.Tejas.TaskManagementSystem.Exception.ErrorResponse;
import com.Tejas.TaskManagementSystem.Service.TaskService;
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
 * Handles task management operations including
 * task creation, assignment, tracking and updates.
 * APIs are secured using JWT authentication and RBAC.
 */
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Tag(
        name = "Task Controller",
        description = "APIs for task creation, management, assignment and tracking"
)
public class TaskController {
    private final TaskService service;

    @Operation(
            summary = "Create new task",
            description = "Creates a new task inside a workspace and assigns it to a user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"
                    ,content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Workspace or assigned user not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TaskResponse> postTask(@Valid @RequestBody TaskRequest request){
        TaskResponse response= service.addTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all tasks",
            description = "Fetches paginated list of all tasks. Accessible only to ADMIN users.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskResponse>> getAllTask(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size
    ){
        List<TaskResponse> responses = service.fetchAllTask(page, size);
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @Operation(
            summary = "Get tasks created by logged-in user",
            description = "Returns all tasks created by currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/postedTask")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<TaskResponse>> getAllTaskPostByUser(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size
    ){
        List<TaskResponse> responses = service.getAllPostedTaskByUser(page, size);
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @Operation(
            summary = "Get tasks assigned to logged-in user",
            description = "Returns all tasks assigned to currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/myTask")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<List<TaskResponse>> getAllTaskAssignedToUser(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size
    ){
        List<TaskResponse> responses = service.getTaskForUser(page,size);
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @Operation(
            summary = "Get task by id",
            description = "Fetches task details using task id.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId){
        TaskResponse response = service.getTaskById(taskId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(
            summary = "Update task",
            description = "Allows task creator to update task details.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized task update",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Task or workspace not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskUpdateRequest request){
        TaskResponse response = service.updateTask(id,request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(
            summary = "Delete task",
            description = "Allows task creator to delete a task.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized task deletion",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        String str = service.deleteTask(id);
        return new ResponseEntity<>(str,HttpStatus.OK);
    }
}
