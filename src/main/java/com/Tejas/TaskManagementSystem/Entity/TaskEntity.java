package com.Tejas.TaskManagementSystem.Entity;

import com.Tejas.TaskManagementSystem.Enum.Priority;
import com.Tejas.TaskManagementSystem.Enum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_task")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "postBy")
    private UserEntity createdBy;

    @ManyToOne
    @JoinColumn(name = "assignedTo")
    private UserEntity assignedUser;

}
