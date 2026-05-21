package com.Tejas.TaskManagementSystem.DTO;

import com.Tejas.TaskManagementSystem.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "Name can't be blank")
    @Size(min = 2, max = 50)
    private String name;
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Invalid email format")
    private String email;
    @Size(min=8,max = 16,message = "Password must be between 8 to 16 character")
    @NotBlank(message = "Password can't be null")
    private String password;
    @NotNull(message = "Role is required")
    private Role role;
}
