package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.AuthDto;
import com.Tejas.TaskManagementSystem.DTO.UserRequest;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Repository.UserRepository;
import com.Tejas.TaskManagementSystem.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; // this will be injected by Spring Container
    private final PasswordEncoder passwordEncoder;// this will be injected from the securityConfig
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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
    public  List<UserResponse> getUserByName(String name){
        List<UserEntity> list = userRepository.findByName(name).orElseThrow(()->new RuntimeException("User Not Found"));
        return list.stream().map(user->toResponse(user)).collect(Collectors.toList());
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
    public UserResponse getUserPublicProfile(String email){
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("No user with email id :"+email));
        return toResponse(user);
    }

    //Update User Profile
    public UserResponse updateUser(Long id,UserRequest request){
        if(id.equals(getLoggedInUser().getId())) {
            UserEntity entity = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
            entity.setName(request.getName());
            entity.setRole(request.getRole());
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(entity);
            return toResponse(entity);
        }else{
            throw new RuntimeException("You cannot update another user's profile");
        }
    }

    public Map<String,Object> authenticateAndGenerateToken(AuthDto authDto){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
            String token = jwtUtil.generateToken(authDto.getEmail());
            return Map.of(
                "User",getUserPublicProfile(authDto.getEmail()),
                "Token",token
            );
        }catch (Exception e){
            return Map.of(
                    "Error",e.getMessage()
            );
        }
    }

    public String deleteUser(Long id){
        try {
            UserEntity entity = userRepository.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
            userRepository.deleteById(entity.getId());
            return "User Deleted";
        }catch (Exception e){
            return "User Not Found";
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
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }
}
