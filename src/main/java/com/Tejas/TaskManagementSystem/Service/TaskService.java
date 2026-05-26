package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.*;
import com.Tejas.TaskManagementSystem.Entity.TaskEntity;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Entity.Workspace;
import com.Tejas.TaskManagementSystem.Enum.Status;
import com.Tejas.TaskManagementSystem.Exception.ResourceNotFoundException;
import com.Tejas.TaskManagementSystem.Exception.UnauthorizedException;
import com.Tejas.TaskManagementSystem.Repository.TaskRepository;
import com.Tejas.TaskManagementSystem.Repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles task management business logic including:
    - task creation and assignment
    - ownership-based authorization
    - task status updates
    - email notifications
 */
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final WorkspaceRepository workspaceRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskResponse addTask(TaskRequest request){
        UserEntity user = userService.getLoggedInUserEntity();
        logger.info("Adding new task with title: {}", request.getTitle());
        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId())
                .orElseThrow(()->{
                    logger.warn("Workspace not found with id: {}", request.getWorkspaceId());
                    return new ResourceNotFoundException("No Workspace found with this id: "+request.getWorkspaceId());
                });
        TaskEntity entity = mapToEntity(request);
        if(user.getId().equals(workspace.getCreatedBy().getId())) {
            if(workspace.getAllocatedUsers().contains(userService.getUserEntity(request.getAssignedUserId()))){
                entity = taskRepository.save(entity);
                logger.info( "Task with id: {} is added successfully in workspace with id: {}",entity.getId(),request.getWorkspaceId());
                String subject = "New Task Assigned To You ";
                String body = """
                    Hello %s,
            
                    A new task has been assigned to you in the Task Management System.
            
                    Task Details:
                    • Title: %s
                    • Description: %s
                    • Priority: %s
                    • Due Date: %s
                    • Workspace: %s
                    Assigned By:
                    • %s
            
                    Please review the task and start working on it before the deadline.
                    Stay focused and productive.
            
                    Best Regards,
                    Task Management System Team
                    """.formatted(
                                    entity.getAssignedUser().getName(),
                                    entity.getTitle(),
                                    entity.getDescription(),
                                    entity.getPriority(),
                                    entity.getDueDate(),
                                    workspace.getName(),
                                    user.getName()
                            );
                emailService.sendEmail(entity.getAssignedUser().getEmail(),subject,body);
                return mapToResponse(entity);
            }else {
                throw new RuntimeException("user with id: "+ request.getAssignedUserId()+" is not the part of workspace with id: "+workspace.getId());
            }

        }else {
            logger.warn("Unauthorized Profile attempt to add task in workspace with id: {}", request.getWorkspaceId());
            throw new UnauthorizedException("You are not allowed to add task in this Workspace: "+workspace.getName());
        }
    }

    public TaskResponse updateTask(Long id, TaskUpdateRequest request){
        logger.info("Attempting to update task with id: {}", id);
        UserResponse loggedInUser = userService.getLoggedInUser();
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: "+id);
                });
        Status oldStatus = entity.getStatus();
        Long loggedInUserId = loggedInUser.getId();
        Long creatorId = entity.getCreatedBy().getId();
        Long assignedUserId = entity.getAssignedUser().getId();
        if(creatorId.equals(loggedInUserId)) {
            //Task creator can ONLY update any feild of task
            if (request.getTitle() != null) {
                entity.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                entity.setDescription(request.getDescription());
            }
            if (request.getPriority() != null) {
                entity.setPriority(request.getPriority());
            }
            if(request.getStatus() !=null){
                entity.setStatus(request.getStatus());
            }
            if (request.getDueDate() != null) {
                entity.setDueDate(request.getDueDate());
            }
            if (request.getAssignedUserId() != null) {
                UserEntity assignedUser = userService.getUserEntity(request.getAssignedUserId());
                entity.setAssignedUser(assignedUser);
            }
            if (request.getWorkspaceId() != null) {
                Workspace workspace = workspaceRepository.findById(request.getWorkspaceId()
                ).orElseThrow(() -> {
                    logger.warn("Workspace not found with id: {}", request.getWorkspaceId());
                    return new ResourceNotFoundException("Workspace not found for id: " + request.getWorkspaceId());
                });
                entity.setWorkspace(workspace);
            }
            taskRepository.save(entity);
            logger.info("Task updated successfully for id: {}", id);
            if(request.getStatus() != null && !oldStatus.equals(request.getStatus())) {
                sendTaskStatusUpdateNotification(entity,oldStatus,loggedInUser);
            }
            return mapToResponse(entity);
        }else if (assignedUserId.equals(loggedInUserId)) {
            // assigned user can ONLY update status
            if (request.getStatus() != null) {
                entity.setStatus(request.getStatus());
                taskRepository.save(entity);
                logger.info("Task status updated successfully for id: {}", id);
                if(request.getStatus() != null && !oldStatus.equals(request.getStatus())) {
                    sendTaskStatusUpdateNotification(entity,oldStatus,loggedInUser);
                }
                return mapToResponse(entity);
            } else {
                throw new UnauthorizedException("You can only update task status");
            }
        }else{
            logger.warn("Unauthorized profile attempting to update task with id {}:", id);
            throw  new UnauthorizedException("You are not allowed to update task with id: "+id);
        }
    }

    public List<TaskResponse> fetchAllTask(int page, int size){
        Pageable pageable= PageRequest.of(page,size);
        Page<TaskEntity> taskPage = taskRepository.findAll(pageable);
        logger.info("Fetching all tasks");
        return taskPage.stream().map(entity -> mapToResponse(entity)).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id){
        TaskEntity entity= taskRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Task not found for id: {}", id);
                    return new ResourceNotFoundException("No Task Found with id: "+id);
                });
        return mapToResponse(entity);
    }

    public List<TaskResponse> getTaskForUser(int page, int size){
        Pageable pageable= PageRequest.of(page,size);
        UserResponse response = userService.getLoggedInUser();
        Page<TaskEntity> page1 = taskRepository.findByAssignedUserId(response.getId(),pageable);
        logger.info("fetching all task of user with id: {}", response.getId());
        return page1.stream().map(entity -> mapToResponse(entity)).collect(Collectors.toList());
    }

    public List<TaskResponse> getAllPostedTaskByUser(int page, int size){
        Pageable pageable= PageRequest.of(page,size);
        UserResponse response = userService.getLoggedInUser();
        Page<TaskEntity> list = taskRepository.findByCreatedById(response.getId(),pageable);
        logger.info("fetching all task posted by user with id: {}", response.getId());
        return list.stream().map(entity -> mapToResponse(entity)).collect(Collectors.toList());
    }

    public String deleteTask(Long id){
        UserResponse response = userService.getLoggedInUser();
        logger.info("Attempting to delete task with id: {}", id);
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found for id: {}", id);
                    return new ResourceNotFoundException("Task Not Found with id: "+id);
                });
            if(entity.getCreatedBy().getId().equals(response.getId())) {
                taskRepository.deleteById(entity.getId());
                logger.info("Task is deleted successfully for id: {}", id);
                return "Task deleted Successfully";
            }else {
                logger.warn("Unauthorized profile attempting to delete task with id: {}", id);
                throw new UnauthorizedException("You are not allowed to delete task with id: "+id);
            }
    }

    public TaskEntity mapToEntity(TaskRequest request){
        UserEntity entity= userService.getLoggedInUserEntity();
        UserEntity assignedUser = userService.getUserEntity(request.getAssignedUserId());
        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId())
                .orElseThrow(()->new ResourceNotFoundException("No Workspace with this id: "+request.getWorkspaceId()));
        return TaskEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .createdBy(entity)
                .assignedUser(assignedUser)
                .dueDate(request.getDueDate())
                .workspace(workspace)
                .build();
    }

    public TaskResponse mapToResponse(TaskEntity entity){
        UserEntity createdBy1= entity.getCreatedBy();
        UserEntity assignedUser1 = entity.getAssignedUser();
        UserResponse createdBy2=  userService.mapToResponse(createdBy1);
        UserResponse assignedUser2 = userService.mapToResponse(assignedUser1);
        WorkspaceResponse workspaceResponse= WorkspaceResponse.builder()
                .name(entity.getWorkspace().getName())
                .description(entity.getWorkspace().getDescription())
                .id(entity.getWorkspace().getId())
                .createdAt(entity.getWorkspace().getCreatedAt())
                .build();
        return TaskResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .createdBy(createdBy2)
                .assignedUser(assignedUser2)
                .dueDate(entity.getDueDate())
                .workspace(workspaceResponse)
                .build();
    }

    //Get taskEntity -> this method is used by comment Service
    public TaskEntity getTaskEntityById(Long id){
        return taskRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Task not found for id{}:",id);
                    return new ResourceNotFoundException("No task exist with id: {}"+id);
                });
    }
    private void sendTaskStatusUpdateNotification(TaskEntity entity, Status oldStatus, UserResponse updatedBy){
        String subject = "Task Status Updated";
        String body = """
            Hello %s,

            The status of a task has been updated.

            Task Details:
            • Title: %s
            • Previous Status: %s
            • Current Status: %s
            • Workspace: %s
            Updated By:
            • %s

            Stay productive 

            Best Regards,
            Task Management System Team
            """;
        // EMAIL TO TASK CREATOR
        emailService.sendEmail(
                entity.getCreatedBy().getEmail(),
                subject,
                body.formatted(
                        entity.getCreatedBy().getName(),
                        entity.getTitle(),
                        oldStatus,
                        entity.getStatus(),
                        entity.getWorkspace().getName(),
                        updatedBy.getName()
                )
        );
        // EMAIL TO ASSIGNED USER
        emailService.sendEmail(
                entity.getAssignedUser().getEmail(),
                subject,
                body.formatted(
                        entity.getAssignedUser().getName(),
                        entity.getTitle(),
                        oldStatus,
                        entity.getStatus(),
                        entity.getWorkspace().getName(),
                        updatedBy.getName()
                )
        );
    }
}
