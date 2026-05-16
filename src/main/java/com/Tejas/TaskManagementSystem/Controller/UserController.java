package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.UserRequest;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping(name = "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    //API for SignUp
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody UserRequest request){
        UserResponse response = service.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //API for get Users
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> responseList = service.getAllUsers();
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmailId(@PathVariable String email){
        UserResponse response = service.getUserByEmail(email);
        return new  ResponseEntity<>(response,HttpStatus.FOUND);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<UserResponse> getUserByName(@PathVariable String name){
        UserResponse response = service.getUserByName(name);
        return new  ResponseEntity<>(response,HttpStatus.FOUND);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        UserResponse response = service.getUserById(id);
        return new  ResponseEntity<>(response,HttpStatus.FOUND);
    }

    //API for update user profile
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request){
        UserResponse response = service.updateUser(id,request);
        return new  ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for delete user
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        String message = service.deleteUser(id);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

}
