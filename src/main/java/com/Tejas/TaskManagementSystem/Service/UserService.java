package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.UserRequest;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; // this will be injected by Spring Container
    private final PasswordEncoder passwordEncoder;// this will be injected from the securityConfig


    //Method for Adding new user
    public UserResponse registerUser(UserRequest request){
        UserEntity entity = toEntity(request);
        entity = userRepository.save(entity);
        return toResponse(entity);
    }

    //Method for getting an user
    public List<UserResponse> getAllUsers(){
        List<UserEntity> list = userRepository.findAll();
        return list.stream().map(user->toResponse(user)).collect(Collectors.toList());
    }
    public  UserResponse getUserById(Long id){
        UserEntity entity = userRepository.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
        return toResponse(entity);
    }
    public  UserResponse getUserByName(String name){
        UserEntity entity = userRepository.findByName(name).orElseThrow(()->new RuntimeException("User Not Found"));
        return toResponse(entity);
    }
    public  UserResponse getUserByEmail(String email){
        UserEntity entity = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        return toResponse(entity);
    }

    //Get Current Logged In User
    public UserResponse getLoggedInUser(){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity entity= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Logged In"));
        return toResponse(entity);
    }

    //Update User Profile
    public UserResponse updateUser(UserRequest request){
        UserEntity entity = toEntity(request);
        entity = userRepository.findByEmail(entity.getEmail()).orElseThrow(()->new RuntimeException("User Not Found"));
        entity.setName(request.getName());
        entity.setRole(request.getRole());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        return toResponse(entity);
    }

    public String deleteUser(String email){
        try {
            UserEntity entity = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
            userRepository.deleteById(entity.getId());
        }catch (Exception e){

        }

    }

    //Helper methods
    private UserEntity toEntity(UserRequest request){
        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
    }

    private UserResponse toResponse(UserEntity entity){
        return UserResponse.builder()
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }
}
