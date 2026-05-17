package com.Tejas.TaskManagementSystem.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthDto {
    private String email;
    private String password;
    private String token;
}
