package com.Tejas.TaskManagementSystem.Entity;

import com.Tejas.TaskManagementSystem.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_user")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<TaskEntity> task;

    @OneToMany(mappedBy = "assignedUser", cascade = CascadeType.ALL)
    private List<TaskEntity> userTask;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Workspace> workspaces;

    @ManyToMany(mappedBy = "allocatedUsers")
    private List<Workspace> workspaceList;

    @OneToMany(mappedBy = "userEntity",cascade = CascadeType.ALL)
    private List<Comment> comments;
}

