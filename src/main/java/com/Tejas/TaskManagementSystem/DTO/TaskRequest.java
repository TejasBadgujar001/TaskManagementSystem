package com.Tejas.TaskManagementSystem.DTO;

import com.Tejas.TaskManagementSystem.Enum.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Task name can't be blank")
    @Size(min = 5, max = 50,message = "Title must be between 5 to 50 character")
    private String title;
    @NotBlank(message = "Task's description can't be blank")
    @Size(min = 5, max = 500,message = "Description  must be between 5 to 500 character")
    private String description;
    @NotNull(message = "Priority can't be null")
    private Priority priority;
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;
    @NotNull(message = "Assigned user must be there")
    private Long assignedUser;
    @NotNull(message = "Workspace can't be null")
    private Long workspace;
}
