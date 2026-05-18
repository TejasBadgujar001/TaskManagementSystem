package com.Tejas.TaskManagementSystem.DTO;

import com.Tejas.TaskManagementSystem.Enum.Role;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSummeryResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
