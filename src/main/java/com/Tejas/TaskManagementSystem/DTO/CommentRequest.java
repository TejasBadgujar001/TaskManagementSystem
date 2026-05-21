package com.Tejas.TaskManagementSystem.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {
    @NotBlank(message = "Content can't be empty")
    @Size(min = 5, max = 500,message = "Comment must be between 5 to 500 characters")
    private String content;
}
