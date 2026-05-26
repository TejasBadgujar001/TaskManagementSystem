package com.Tejas.TaskManagementSystem.DTO;

import com.Tejas.TaskManagementSystem.Enum.Priority;
import com.Tejas.TaskManagementSystem.Enum.Status;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TaskUpdateRequest {
    @Size(min = 5, max = 50,message = "Title must be between 5 to 50 character")
    private String title;
    @Size(min = 5, max = 500,message = "Description  must be between 5 to 500 character")
    private String description;
    private Priority priority;
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;
    private Long assignedUserId;
    private Long workspaceId;
    private Status status;
}
