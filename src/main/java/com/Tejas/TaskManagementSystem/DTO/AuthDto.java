package com.Tejas.TaskManagementSystem.DTO;

import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDto {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email can't be null")
    private String email;
    @Size(min=8,max = 16,message = "Password must be between 8 to 16 characters")
    @NotBlank(message = "Password can't be null")
    private String password;
}
