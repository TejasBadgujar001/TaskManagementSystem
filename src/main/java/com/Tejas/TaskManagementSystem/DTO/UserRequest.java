package com.Tejas.TaskManagementSystem.DTO;

import com.Tejas.TaskManagementSystem.Enum.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}
