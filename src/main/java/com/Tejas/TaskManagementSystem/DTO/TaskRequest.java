package com.Tejas.TaskManagementSystem.DTO;

import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Enum.Priority;
import com.Tejas.TaskManagementSystem.Enum.Status;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Long assignedUser;
}
