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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    //Method for Adding new user
    public UserResponse registerUser(UserRequest request){
        UserEntity entity = toEntity(request);
        logger.info("Registering new user with email: {}", request.getEmail());
        entity = userRepository.save(entity);
        logger.info("User registered successfully with id: {}", entity.getId());
        return toResponse(entity);
    }

    //Method for getting an user
    public List<UserResponse> getAllUsers(){
        List<UserEntity> list = userRepository.findAll();
        logger.info("Fetching all users");
        return list.stream().map(user->toResponse(user)).collect(Collectors.toList());
    }
    public  UserResponse getUserById(Long id){
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User Not Found with id"+id);
                });
        logger.info("Fetching user with id: {}", id);
        return toResponse(entity);
    }
    public  List<UserResponse> getUserByName(String name){
        List<UserEntity> list = userRepository.findByName(name)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found with name: "+name));
        return list.stream().map(user->toResponse(user)).collect(Collectors.toList());
    }
    public  UserResponse getUserByEmail(String email){
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(()->{
                    logger.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User Not Found with email: "+email);
                });
        logger.info("Fetching user with email: {}", email);
        return toResponse(entity);
    }

    //Get Current Logged In User
    public UserResponse getLoggedInUser(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity entity= userRepository.findByEmail(email)
                .orElseThrow(()->{
                    logger.error("Authenticated user not found in database");
                    return new RuntimeException("User Not Logged In");
                });
        logger.info("Fetching currently logged in user");
        return toResponse(entity);
    }
    public UserEntity getLoggedInUserEntity(){
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity entity= userRepository.findByEmail(email)
                .orElseThrow(()->new UnauthorizedException("User Not Logged In"));
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
        logger.info("Attempting to update user profile with id: {}", id);
        if(id.equals(getLoggedInUser().getId())) {
            UserEntity entity = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("User not found with email: {}", id);
                        return new ResourceNotFoundException("User Not Found with id:  "+id);
                    });
            entity.setName(request.getName());
            entity.setRole(request.getRole());
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(entity);
            logger.info("User profile updated successfully for id: {}", id);
            return toResponse(entity);
        }else{
            logger.warn("Unauthorized profile update attempt for user id: {}", id);
            throw new UnauthorizedException("You cannot update another user's profile");
        }
    }

    public Map<String,Object> authenticateAndGenerateToken(AuthDto authDto){
        logger.info("Authenticating user with email: {}", authDto.getEmail());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
        String token = jwtUtil.generateToken(authDto.getEmail());
        logger.info("JWT token generated successfully for user: {}", authDto.getEmail());
        return Map.of(
                "User",getUserPublicProfile(authDto.getEmail()),
                "Token",token
        );
    }

    public String deleteUser(Long id){
        logger.info("Attempting to delete user with id: {}", id);
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Delete failed. User not found with id: {}", id);
                    return new ResourceNotFoundException("User Not Found with id: "+id);
                });
        userRepository.deleteById(entity.getId());
        logger.info("User deleted successfully with id: {}", id);
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
