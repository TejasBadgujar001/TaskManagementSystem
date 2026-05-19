package com.Tejas.TaskManagementSystem.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WorkspaceResponse {
    private Long id;
    private String  name;
    private String description;
    private UserResponse createdBy;
    private LocalDateTime createdAt;
    private List<TaskResponse> tasks;
    private List<UserResponse> allocatedUser;
}
