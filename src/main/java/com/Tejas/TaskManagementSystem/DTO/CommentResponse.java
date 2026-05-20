package com.Tejas.TaskManagementSystem.DTO;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private String content;
    private String taskName;
    private String userName;
    private String userEmail;
}
