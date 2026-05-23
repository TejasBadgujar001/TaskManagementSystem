package com.Tejas.TaskManagementSystem.Repository;

import com.Tejas.TaskManagementSystem.Entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity,Long> {

    Page<TaskEntity> findByCreatedById(Long id, Pageable pageable);
    Page<TaskEntity> findByAssignedUserId(Long id,Pageable pageable);
}
