package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.*;
import com.Tejas.TaskManagementSystem.Entity.TaskEntity;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Entity.Workspace;
import com.Tejas.TaskManagementSystem.Exception.ResourceNotFoundException;
import com.Tejas.TaskManagementSystem.Exception.UnauthorizedException;
import com.Tejas.TaskManagementSystem.Repository.TaskRepository;
import com.Tejas.TaskManagementSystem.Repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final WorkspaceRepository workspaceRepository;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    //Post Task
    public TaskResponse addTask(TaskRequest request){
        UserEntity user = userService.getLoggedInUserEntity();
        logger.info("Adding new task with title: {}", request.getTitle());
        Workspace workspace = workspaceRepository.findById(request.getWorkspace())
                .orElseThrow(()->{
                    logger.warn("Workspace not found with id: {}", request.getWorkspace());
                    return new ResourceNotFoundException("No Workspace found with this id: "+request.getWorkspace());
                });
        if(user.getId().equals(workspace.getCreatedBy().getId())) {
            TaskEntity entity = toEntity(request);
            entity = taskRepository.save(entity);
            logger.info( "Task with id: {} is added successfully in workspace with id: {}",entity.getId(),request.getWorkspace());
            return toResponse(entity);
        }else {
            logger.warn("Unauthorized Profile attempt to add task in workspace with id: {}", request.getWorkspace());
            throw new UnauthorizedException("You are not allowed to add task in this Workspace: "+workspace.getName());
        }
    }

    //Update Task
    public TaskResponse updateTask(Long id, TaskUpdateRequest request){
        logger.info("Attempting to update task with id: {}", id);
        UserResponse response = userService.getLoggedInUser();
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: "+id);
                });
        if(entity.getCreatedBy().getId().equals(response.getId())) {
            if (request.getTitle() != null) {
                entity.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                entity.setDescription(request.getDescription());
            }
            if (request.getPriority() != null) {
                entity.setPriority(request.getPriority());
            }
            if (request.getStatus() !=null){
                entity.setStatus(request.getStatus());
            }
            if (request.getDueDate() != null) {
                entity.setDueDate(request.getDueDate());
            }
            if (request.getAssignedUser() != null) {
                UserEntity assignedUser = userService.getUserEntity(request.getAssignedUser());
                entity.setAssignedUser(assignedUser);
            }
            if (request.getWorkspace() != null) {
                Workspace workspace = workspaceRepository.findById(request.getWorkspace()
                ).orElseThrow(() -> {
                    logger.warn("Workspace not found with id: {}", request.getWorkspace());
                    return new ResourceNotFoundException("Workspace not found for id: " + request.getWorkspace());
                });
                entity.setWorkspace(workspace);
            }
            taskRepository.save(entity);
            logger.info("Task updated successfully for id: {}", id);
            return toResponse(entity);
        }else{
            logger.warn("Unauthorized profile attempting to update task with id {}:", id);
            throw new UnauthorizedException("You are not allowed to update task with id: "+id);
        }
    }

    //Methods for fetching Task
    public List<TaskResponse> fetchAllTask(){
        List<TaskEntity> entities = taskRepository.findAll();
        logger.info("Fetching all tasks");
        return entities.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id){
        TaskEntity entity= taskRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Task not found for id: {}", id);
                    return new ResourceNotFoundException("No Task Found with id: "+id);
                });
        return toResponse(entity);
    }

    public List<TaskResponse> getTaskForUser(){
        UserResponse response = userService.getLoggedInUser();
        List<TaskEntity> list = taskRepository.findByAssignedUserId(response.getId());
        logger.info("fetching all task of user with id: {}", response.getId());
        return list.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    public List<TaskResponse> getAllPostedTaskByUser(){
        UserResponse response = userService.getLoggedInUser();
        List<TaskEntity> list = taskRepository.findByCreatedById(response.getId());
        logger.info("fetching all task posted by user with id: {}", response.getId());
        return list.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    //Delete Task
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


    //Helper methods
    public TaskEntity toEntity(TaskRequest request){
        UserEntity entity= userService.getLoggedInUserEntity();
        UserEntity assignedUser = userService.getUserEntity(request.getAssignedUser());
        Workspace workspace = workspaceRepository.findById(request.getWorkspace())
                .orElseThrow(()->new ResourceNotFoundException("No Workspace with this id: "+request.getWorkspace()));
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

    public TaskResponse toResponse(TaskEntity entity){
        UserEntity createdBy1= entity.getCreatedBy();
        UserEntity assignedUser1 = entity.getAssignedUser();
        UserResponse createdBy2=  userService.toResponse(createdBy1);
        UserResponse assignedUser2 = userService.toResponse(assignedUser1);
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
}
