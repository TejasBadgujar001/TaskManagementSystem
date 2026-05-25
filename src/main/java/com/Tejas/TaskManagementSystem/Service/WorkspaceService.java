package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.*;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Entity.Workspace;
import com.Tejas.TaskManagementSystem.Exception.ResourceNotFoundException;
import com.Tejas.TaskManagementSystem.Exception.UnauthorizedException;
import com.Tejas.TaskManagementSystem.Repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceService.class);

    //Create Workspace
    public WorkspaceResponse createWorkspace(WorkspaceRequest request){
        logger.info("Creating workspace with name : {}", request.getName());
        Workspace entity = toEntity(request);
        entity = workspaceRepository.save(entity);
        logger.info("Created workspace successfully with name : {}", request.getName());
        return toResponse(entity);
    }

    //Add Users to Workspace
    public WorkspaceResponse addUserToWorkspace(Long id, List<Long> ids){
        UserResponse response = userService.getLoggedInUser();
        logger.info("Attempting to add users in workspace with id: {}",id);
        Workspace workspace= workspaceRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Workspace not found for id: {}", id);
                    return new ResourceNotFoundException("No Workspace exist with id: "+id);
                });
        if(response.getId().equals(workspace.getCreatedBy().getId())) {
            List<UserEntity> userEntities = userService.getAllUserByIds(ids);
            if(workspace.getAllocatedUsers() == null){
                workspace.setAllocatedUsers(userEntities);
            }else{
                workspace.getAllocatedUsers().addAll(userEntities);
            }
            workspace = workspaceRepository.save(workspace);
            logger.info("User with ids {} are added to workspace with id: {}",ids,id);
            for(UserEntity user : userEntities){
                String subject = "You have been added to a workspace ";
                String body = """
                    Hello %s,
                    
                    You have been added to the workspace:
                    Workspace Name: %s
                    Description: %s
                    Added By: %s
                    
                    You can now collaborate, manage tasks, and track progress inside the workspace.
                    Stay productive
                    
                    Regards,
                    Task Management System
                    """
                        .formatted(
                                user.getName(),
                                workspace.getName(),
                                workspace.getDescription(),
                                workspace.getCreatedBy().getName()
                        );
                emailService.sendEmail(user.getEmail(), subject, body);
            }
            return toResponse(workspace);
        }else{
            logger.warn("Unauthorized profile attempting to add users to workspace with id: {}", id);
            throw new UnauthorizedException("You are not allowed to add users to workspace with id: "+id);
        }
    }

    //GetAllWorkspace
    public List<WorkspaceResponse> getAllWorkspace(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        logger.info("Fetching all workspaces");
         List<WorkspaceResponse> responses = workspaceRepository.findAll(pageable)
                 .stream().map(workspace -> toResponse(workspace))
                 .collect(Collectors.toList());
         return responses;
    }

    //GetWorkspaceById
    public WorkspaceResponse getWorkspaceById(Long id){
        logger.info("Fetching workspace for id: {}", id);
        Workspace entity = workspaceRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Workspace not found for id: {}",id);
                    return new ResourceNotFoundException("No Workspace found with this id "+id);
                });
        return toResponse(entity);
    }

    //GetWorkspaceByName
    public WorkspaceResponse getWorkspaceByName(String  name){
        logger.info("Fetching workspace for name: {}", name);
        Workspace entity = workspaceRepository.findByName(name)
                .orElseThrow(()->{
                    logger.warn("Workspace not found for name: {}",name);
                    return new ResourceNotFoundException("No Workspace with name: "+name);
                });
        return toResponse(entity);
    }

    //Update Workspace
    public WorkspaceResponse updateWorkspace(Long id, WorkspaceUpdateRequest request){
        logger.info("Attempting to update workspace for id: {}",id);
        UserResponse loggedInUser = userService.getLoggedInUser();
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Workspace not found for id: {}",id);
                    return new ResourceNotFoundException("No Workspace exist with this id: "+id);
                });
        if(loggedInUser.getId().equals(workspace.getCreatedBy().getId())){
            if(request.getName() != null){
                workspace.setName(request.getName());
            }
            if(request.getDescription() != null){
                workspace.setDescription(request.getDescription());
            }
            workspaceRepository.save(workspace);
            logger.info("Workspace Updated successfully for id: {}", id);
            return toResponse(workspace);
        }else{
            logger.warn("Unauthorized profile attempting to update workspace with id: {}", id);
            throw new UnauthorizedException("You are not allowed to update workspace with id: "+id);
        }
    }
    //Delete Workspace
    public String deleteWorkspace(Long id){
        logger.info("Attempting to delete  workspace for id: {}",id);
        UserResponse loggedInUser = userService.getLoggedInUser();
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Workspace not found for id: {}",id);
                    return new ResourceNotFoundException("No Workspace exist with this id: "+id);
                });
        if(loggedInUser.getId().equals(workspace.getCreatedBy().getId())){
            workspaceRepository.deleteById(id);
            logger.info("Workspace deleted successfully for id: {}", id);
            return "Workspace deleted.";
        }else{
            logger.warn("Unauthorized profile attempting to delete workspace with id: {}", id);
            throw new UnauthorizedException("You are not allowed to delete this workspace");
        }
    }

    //Get All Users of Workspace
    public List<UserResponse> getAllUserOfWorkspace(Long id){
        Workspace workspace =  workspaceRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Workspace not found for id: {}", id);
                    return new ResourceNotFoundException("No workspace exist with this id: "+id);
                });
        List<UserResponse> responses = workspace.getAllocatedUsers()
                .stream().map(user->userService.toResponse(user)).collect(Collectors.toList());
        logger.info("Fetching all users of workspace with id: {}", id);
        return responses;
    }

    //Get All Task of Workspace
    public List<TaskResponse> getAllTaskOfWorkspace(Long id){
        Workspace workspace =  workspaceRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Workspace not found for id: {}", id);
                    return new ResourceNotFoundException("No workspace exist with this id: "+id);
                });
        List<TaskResponse> responses = workspace.getTasks()
                .stream().map(task->taskService.toResponse(task)).collect(Collectors.toList());
        logger.info("Fetching all tasks of workspace with id: {}", id);
        return responses;
    }

    //Helper method
    public Workspace toEntity(WorkspaceRequest request){
        return Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(userService.getLoggedInUserEntity())
                .build();
    }

    public WorkspaceResponse toResponse(Workspace entity){
        UserResponse response = UserResponse.builder()
                .name(entity.getCreatedBy().getName())
                .email(entity.getCreatedBy().getEmail())
                .id(entity.getCreatedBy().getId())
                .role(entity.getCreatedBy().getRole())
                .build();

        List<UserResponse> users =
                entity.getAllocatedUsers() == null
                ? List.of()
                :entity.getAllocatedUsers().stream()
                        .map(user -> UserResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build())
                        .collect(Collectors.toList());

        List<TaskResponse> tasks =
                entity.getTasks()==null
                        ? List.of()
                        :entity.getTasks().stream()
                        .map(task -> TaskResponse.builder()
                                .id(task.getId())
                                .title(task.getTitle())
                                .description(task.getDescription())
                                .priority(task.getPriority())
                                .status(task.getStatus())
                                .dueDate(task.getDueDate())
                                .build())
                        .collect(Collectors.toList());

        return WorkspaceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdBy(response)
                .createdAt(entity.getCreatedAt())
                .allocatedUser(users)
                .tasks(tasks)
                .build();
    }
    public Workspace getWorkspaceEntity(Long id){
        return workspaceRepository.findById(id
        ).orElseThrow(()->{
            logger.warn("Workspace not found for id: {}", id);
            return new ResourceNotFoundException("No workspace exist with id: "+id);
        });
    }
}
