package com.Tejas.TaskManagementSystem.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class WorkspaceRequest {
    private String  name;
    private String description;
}
