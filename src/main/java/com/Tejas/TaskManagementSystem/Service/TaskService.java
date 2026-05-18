package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.TaskRequest;
import com.Tejas.TaskManagementSystem.DTO.TaskResponse;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.Entity.TaskEntity;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    //Post Task
    public TaskResponse addTask(TaskRequest request){
        TaskEntity entity = toEntity(request);
        entity = taskRepository.save(entity);
        return toResponse(entity);
    }

    //Update Task
    public TaskResponse updateTask(Long id, TaskRequest request){
        UserResponse response = userService.getLoggedInUser();
        TaskEntity entity = taskRepository.findById(id).orElseThrow(()->new RuntimeException("Task not found with id: "+id));
        if(entity.getCreatedBy().getId().equals(response.getId())) {
            entity.setTitle(request.getTitle());
            entity.setPriority(request.getPriority());
            entity.setDescription(request.getDescription());
            entity.setDueDate(request.getDueDate());
            entity.setStatus(request.getStatus());
            entity.setAssignedUser(request.getAssignedUser());
            taskRepository.save(entity);
            return toResponse(entity);
        }else{
            throw new RuntimeException("You are not allowed to update this task");
        }
    }

    //Methods for fetching Task
    public List<TaskResponse> fetchAllTask(){
        List<TaskEntity> entities = taskRepository.findAll();
        return entities.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id){
        TaskEntity entity= taskRepository.findById(id).orElseThrow(()->new RuntimeException("No Task Found"));
        return toResponse(entity);
    }

    public List<TaskResponse> getTaskForUser(){
        UserResponse response = userService.getLoggedInUser();
        List<TaskEntity> list = taskRepository.findByAssignedUserId(response.getId()).orElseThrow(()->new RuntimeException("No task assigned"));
        return list.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    public List<TaskResponse> getAllPostedTaskByUser(){
        UserResponse response = userService.getLoggedInUser();
        List<TaskEntity> list = taskRepository.findByCreatedById(response.getId()).orElseThrow(()->new RuntimeException("No task Posted By You"));
        return list.stream().map(entity -> toResponse(entity)).collect(Collectors.toList());
    }

    //Delete Task
    public String deleteTask(Long id){
        UserResponse response = userService.getLoggedInUser();
        TaskEntity entity = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not exist"));
            if(entity.getCreatedBy().getId().equals(response.getId())) {
                taskRepository.deleteById(entity.getId());
                return "Task deleted";
            }else {
                throw new RuntimeException("You are not allowed to delete this task");
            }
    }


    //Helper methods
    private TaskEntity toEntity(TaskRequest request){
        UserEntity entity= userService.getLoggedInUserEntity();
        return TaskEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus())
                .createdBy(entity)
                .assignedUser(request.getAssignedUser())
                .dueDate(request.getDueDate())
                .build();
    }

    private TaskResponse toResponse(TaskEntity entity){
        return TaskResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .assignedUser(entity.getAssignedUser())
                .dueDate(entity.getDueDate())
                .build();
    }
}
