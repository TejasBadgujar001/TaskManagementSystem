package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.AuthDto;
import com.Tejas.TaskManagementSystem.DTO.UserRequest;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Exception.ResourceNotFoundException;
import com.Tejas.TaskManagementSystem.Exception.UnauthorizedException;
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
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found with id"+id));
        return toResponse(entity);
    }
    public  List<UserResponse> getUserByName(String name){
        List<UserEntity> list = userRepository.findByName(name)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found with name: "+name));
        return list.stream().map(user->toResponse(user)).collect(Collectors.toList());
    }
    public  UserResponse getUserByEmail(String email){
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found with email: "+email));
        return toResponse(entity);
    }

    //Get Current Logged In User
    public UserResponse getLoggedInUser(){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity entity= userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User Not Logged In"));
        return toResponse(entity);
    }
    public UserEntity getLoggedInUserEntity(){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity entity= userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User Not Logged In"));
        return entity;
    }
    public UserEntity getUserEntity(Long id){
        return userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found with id: "+id));
    }

    public UserResponse getUserPublicProfile(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("No user with email id :"+email));
        return toResponse(user);
    }
    //this method user in workspace Service to add users for workspace
    public List<UserEntity> getAllUserByIds(List<Long>ids){
         return userRepository.findAllById(ids);
    }

    //Update User Profile
    public UserResponse updateUser(Long id,UserRequest request){
        if(id.equals(getLoggedInUser().getId())) {
            UserEntity entity = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User Not Found with id:  "+id));
            entity.setName(request.getName());
            entity.setRole(request.getRole());
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(entity);
            return toResponse(entity);
        }else{
            throw new UnauthorizedException("You cannot update another user's profile");
        }
    }

    public Map<String,Object> authenticateAndGenerateToken(AuthDto authDto){
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
            String token = jwtUtil.generateToken(authDto.getEmail());
            return Map.of(
                "User",getUserPublicProfile(authDto.getEmail()),
                "Token",token
            );
    }

    public String deleteUser(Long id){
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found with id: "+id));
        userRepository.deleteById(entity.getId());
        return "User Deleted Successfully";
    }

    //Helper methods
    public UserEntity toEntity(UserRequest request){
        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
    }

    public UserResponse toResponse(UserEntity entity){
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }
}
