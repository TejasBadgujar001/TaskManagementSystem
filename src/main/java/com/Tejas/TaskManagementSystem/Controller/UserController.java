package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.AuthDto;
import com.Tejas.TaskManagementSystem.DTO.UserRequest;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.DTO.UserUpdateRequest;
import com.Tejas.TaskManagementSystem.Exception.ErrorResponse;
import com.Tejas.TaskManagementSystem.Service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "APIs for user management, authentication and profile operations")
public class UserController {
    private final UserService service;

    //API for SignUp
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody UserRequest request){
        UserResponse response = service.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //API for get Users
    @Operation(
            summary = "Fetch all users",
            description = "Returns paginated list of all users. Accessible only to ADMIN users.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
        ){
        List<UserResponse> responseList = service.getAllUsers(page,size);
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }

    //API for fetching user by email
    @Operation(
            summary = "Get user by email",
            description = "Fetch user details using email address",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<UserResponse> getUserByEmailId(@PathVariable String email){
        UserResponse response = service.getUserByEmail(email);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for fetching user by name
    @Operation(
            summary = "Search users by name",
            description = "Returns list of users matching provided name",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<List<UserResponse>> getUserByName(@PathVariable String name){
        List<UserResponse> response = service.getUserByName(name);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for fetching user by id
    @Operation(
            summary = "Get user by id",
            description = "Fetch user details using user id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        UserResponse response = service.getUserById(id);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for update user profile
    @Operation(
            summary = "Update user profile",
            description = "Allows logged-in user to partially update their profile information",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized profile update",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateRequest request){
        UserResponse response = service.updateUser(id,request);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for delete user
    @Operation(
            summary = "Delete user",
            description = "Deletes user account using user id. Accessible only to ADMIN users.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        String message = service.deleteUser(id);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    //API for Login
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user credentials and generates JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content =  @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> signIn(@Valid @RequestBody AuthDto dto){
        Map<String,Object> res = service.authenticateAndGenerateToken(dto);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }
}
