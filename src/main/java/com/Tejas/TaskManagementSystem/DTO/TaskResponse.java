package com.Tejas.TaskManagementSystem.DTO;

import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Enum.Priority;
import com.Tejas.TaskManagementSystem.Enum.Status;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private UserResponse createdBy;
    private UserResponse assignedUser;
    private WorkspaceResponse workspace;
}
