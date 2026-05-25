package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.CommentRequest;
import com.Tejas.TaskManagementSystem.DTO.CommentResponse;
import com.Tejas.TaskManagementSystem.Exception.ErrorResponse;
import com.Tejas.TaskManagementSystem.Service.CommentService;
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

@RestController
@RequestMapping(path = "/comment")
@RequiredArgsConstructor
@Tag(
        name = "Comment Controller",
        description = "APIs for managing task comments and discussion"
)
public class CommentController {
    private final CommentService service;

    //API for posting comments
    @Operation(
            summary = "Add comment to task",
            description = "Allows MANAGER or MEMBER to add a comment on a task.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment added successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
    @PostMapping("/post/{id}")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long id, @Valid @RequestBody CommentRequest request){
        CommentResponse response = service.addComment(id,request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //API for updating comment
    @Operation(
            summary = "Update comment",
            description = "Allows comment owner to edit their comment.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized comment update",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id,@Valid @RequestBody CommentRequest request){
        CommentResponse response = service.updateComment(id,request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API for deleting comment
    @Operation(
            summary = "Delete comment",
            description = "Allows comment owner to delete their comment.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized comment deletion",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id){
        String response = service.deleteComment(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API for fetching all comments for task
    @Operation(
            summary = "Get all comments for task",
            description = "Fetches paginated list of all comments associated with a task.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/task/{id}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForTask(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return  new ResponseEntity<>(service.getAllCommentsForTask(id,page,size),HttpStatus.OK);
    }
    //API for fetching all comments for user
    @Operation(
            summary = "Get all comments of user",
            description = "Fetches paginated list of comments created by a user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments fetched successfully"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Unauthorized access",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasAnyRole('MEMBER','MANAGER')")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForUser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return  new ResponseEntity<>(service.getAllCommentsForUser(id,page,size),HttpStatus.OK);
    }

}
