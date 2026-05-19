package com.Tejas.TaskManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "tbl_workspace")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(length = 100)
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private UserEntity createdBy;

    @OneToMany(mappedBy = "workspace",cascade = CascadeType.ALL)
    private List<TaskEntity> tasks;

    @ManyToMany
    @JoinTable(
            name = "workspace_user",
            joinColumns = @JoinColumn(name = "workspaceId"),
            inverseJoinColumns =@JoinColumn(name = "userId")
    )
    private List<UserEntity>allocatedUsers;

}
