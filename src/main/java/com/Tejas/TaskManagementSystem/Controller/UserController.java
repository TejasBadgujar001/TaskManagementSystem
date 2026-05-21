package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.AuthDto;
import com.Tejas.TaskManagementSystem.DTO.UserRequest;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    //API for SignUp
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@Valid @RequestBody UserRequest request){
        UserResponse response = service.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //API for get Users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> responseList = service.getAllUsers();
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<UserResponse> getUserByEmailId(@PathVariable String email){
        UserResponse response = service.getUserByEmail(email);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<List<UserResponse>> getUserByName(@PathVariable String name){
        List<UserResponse> response = service.getUserByName(name);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        UserResponse response = service.getUserById(id);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for update user profile
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@Valid @RequestBody UserRequest request){
        UserResponse response = service.updateUser(id,request);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for delete user
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        String message = service.deleteUser(id);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    //API for Login
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> signIn(@Valid @RequestBody AuthDto dto){
        Map<String,Object> res = service.authenticateAndGenerateToken(dto);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }
}
