package com.Tejas.TaskManagementSystem.Repository;

import com.Tejas.TaskManagementSystem.Entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity,Long> {

    Optional<List<TaskEntity>> findByCreatedBy(Long id);
    Optional<List<TaskEntity>> findByAssignedUser(Long id);
}
