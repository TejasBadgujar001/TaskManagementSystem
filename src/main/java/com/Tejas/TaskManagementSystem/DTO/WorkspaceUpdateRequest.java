package com.Tejas.TaskManagementSystem.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class WorkspaceUpdateRequest {
    @Size(min=3,max = 50, message = "Workspace name too long")
    private String  name;
    @Size(max = 100, message = "Description should be up to 100 characters")
    private String description;
}
