package com.Tejas.TaskManagementSystem.DTO;

import com.Tejas.TaskManagementSystem.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserUpdateRequest {
    @Size(min = 2, max = 50)
    private String name;
    @Email(message = "Invalid email format")
    private String email;
    @Size(min=8,max = 16,message = "Password must be between 8 to 16 character")
    private String password;
    private Role role;
}
