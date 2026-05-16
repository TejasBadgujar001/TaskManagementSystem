package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.TaskRequest;
import com.Tejas.TaskManagementSystem.DTO.TaskResponse;
import com.Tejas.TaskManagementSystem.Entity.TaskEntity;
import com.Tejas.TaskManagementSystem.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    //Post Task
    public TaskResponse addTask(TaskRequest request){
        TaskEntity entity = toEntity(request);
        entity = taskRepository.save(entity);
        return toResponse(entity);
    }

    //Update Task


    //Helper methods
    private TaskEntity toEntity(TaskRequest request){
        return TaskEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus())
                .createdBy(request.getCreatedBy())
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
