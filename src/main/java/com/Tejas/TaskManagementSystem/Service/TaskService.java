package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.TaskRequest;
import com.Tejas.TaskManagementSystem.DTO.TaskResponse;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.DTO.WorkspaceResponse;
import com.Tejas.TaskManagementSystem.Entity.TaskEntity;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Entity.Workspace;
import com.Tejas.TaskManagementSystem.Exception.ResourceNotFoundException;
import com.Tejas.TaskManagementSystem.Exception.UnauthorizedException;
import com.Tejas.TaskManagementSystem.Repository.TaskRepository;
import com.Tejas.TaskManagementSystem.Repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final WorkspaceRepository workspaceRepository;

    //Post Task
    public TaskResponse addTask(TaskRequest request){
        UserEntity user = userService.getLoggedInUserEntity();
        Workspace workspace = workspaceRepository.findById(request.getWorkspace())
                .orElseThrow(()->new ResourceNotFoundException("No Workspace found with this id: "+request.getWorkspace()));
        if(user.getId().equals(workspace.getCreatedBy().getId())) {
            TaskEntity entity = toEntity(request);
            entity = taskRepository.save(entity);
            return toResponse(entity);
        }else {
            throw new UnauthorizedException("You are not allowed to add task in this Workspace: "+workspace.getName());
        }
    }

    //Update Task
    public TaskResponse updateTask(Long id, TaskRequest request){
        UserResponse response = userService.getLoggedInUser();
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Task not found with id: "+id));
        UserEntity assignedUser = userService.getUserEntity(request.getAssignedUser());
        if(entity.getCreatedBy().getId().equals(response.getId())) {
            entity.setTitle(request.getTitle());
            entity.setPriority(request.getPriority());
            entity.setDescription(request.getDescription());
            entity.setDueDate(request.getDueDate());
            entity.setAssignedUser(assignedUser);
            taskRepository.save(entity);
            return toResponse(entity);
        }else{
            throw new UnauthorizedException("You are not allowed to update task with id: "+id);
        }
    }

    //Methods for fetching Task
    public List<TaskResponse> fetchAllTask(){
        List<TaskEntity> entities = taskRepository.findAll();
        return entities.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id){
        TaskEntity entity= taskRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No Task Found with id: "+id));
        return toResponse(entity);
    }

    public List<TaskResponse> getTaskForUser(){
        UserResponse response = userService.getLoggedInUser();
        List<TaskEntity> list = taskRepository.findByAssignedUserId(response.getId());
        return list.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    public List<TaskResponse> getAllPostedTaskByUser(){
        UserResponse response = userService.getLoggedInUser();
        List<TaskEntity> list = taskRepository.findByCreatedById(response.getId());
        return list.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    //Delete Task
    public String deleteTask(Long id){
        UserResponse response = userService.getLoggedInUser();
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Not Found with id: "+id));
            if(entity.getCreatedBy().getId().equals(response.getId())) {
                taskRepository.deleteById(entity.getId());
                return "Task deleted Successfully";
            }else {
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
                .orElseThrow(()->new ResourceNotFoundException("No task exist with id: "+id));
    }
}
