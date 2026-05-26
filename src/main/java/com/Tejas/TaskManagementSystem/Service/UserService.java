package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.AuthDto;
import com.Tejas.TaskManagementSystem.DTO.UserRequest;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.DTO.UserUpdateRequest;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Enum.Role;
import com.Tejas.TaskManagementSystem.Exception.ResourceNotFoundException;
import com.Tejas.TaskManagementSystem.Exception.UnauthorizedException;
import com.Tejas.TaskManagementSystem.Repository.UserRepository;
import com.Tejas.TaskManagementSystem.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles user management, authentication,
 * profile operations and JWT token generation.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserResponse registerUser(UserRequest request){
        UserEntity entity = mapToEntity(request);
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }
        logger.info("Registering new user with email: {}", request.getEmail());
        entity = userRepository.save(entity);
        logger.info("User registered successfully with id: {}", entity.getId());
        String subject ="Welcome to Task Management System \uD83C\uDF89";
        String body ="""
            Hello %s,
    
            Welcome to Task Management System! 🎉
    
            Your account has been created successfully, and you're now ready to organize tasks, collaborate with your team, and manage work more efficiently.
            
            With Task Management System, you can:
            • Create and manage tasks
            • Collaborate within workspaces
            • Track task progress
            • Assign tasks to team members
            • Stay productive and organized
            
            We're excited to have you onboard.
            Happy Productivity 
            
            Best Regards,
            Task Management System Team
            """.formatted(request.getName());
        String to = request.getEmail();
        emailService.sendEmail(to,subject,body);
        return mapToResponse(entity);
    }

    public List<UserResponse> getAllUsers(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<UserEntity> page1 = userRepository.findAll(pageable);
        logger.info("Fetching all users");
        return page1.stream().map(user-> mapToResponse(user)).collect(Collectors.toList());
    }

    public  UserResponse getUserById(Long id){
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User Not Found with id: {}"+id);
                });
        logger.info("Fetching user with id: {}", id);
        return mapToResponse(entity);
    }

    public  List<UserResponse> getUserByName(String name){
        List<UserEntity> list = userRepository.findByName(name)
                .orElseThrow(()->new ResourceNotFoundException("User Not Found with name: "+name));
        return list.stream().map(user-> mapToResponse(user)).collect(Collectors.toList());
    }

    public  UserResponse getUserByEmail(String email){
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(()->{
                    logger.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User Not Found with email: "+email);
                });
        logger.info("Fetching user with email: {}", email);
        return mapToResponse(entity);
    }

    public UserResponse getLoggedInUser(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity entity= userRepository.findByEmail(email)
                .orElseThrow(()->{
                    logger.error("Authenticated user not found in database");
                    return new UnauthorizedException("User Not Logged In");
                });
        logger.info("Fetching currently logged in user");
        return mapToResponse(entity);
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
        return mapToResponse(user);
    }

    public List<UserEntity> getAllUserByIds(List<Long>ids){
         return userRepository.findAllById(ids);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request){
        logger.info("Attempting to update user profile with id: {}", id);
        if(id.equals(getLoggedInUser().getId())) {
            UserEntity entity = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("User not found with email: {}", id);
                        return new ResourceNotFoundException("User Not Found with id:  "+id);
                    });
            if (request.getName() != null) {
                entity.setName(request.getName());
            }
            if(request.getRole() != null){
                if(getLoggedInUser().getRole() == Role.ADMIN){
                    entity.setRole(request.getRole());
                }else{
                    throw new UnauthorizedException(
                            "Only admin can change roles"
                    );
                }
            }
            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                entity.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            userRepository.save(entity);
            logger.info("User profile updated successfully for id: {}", id);
            return mapToResponse(entity);
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

    public UserEntity mapToEntity(UserRequest request){
        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
    }

    public UserResponse mapToResponse(UserEntity entity){
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }
}
