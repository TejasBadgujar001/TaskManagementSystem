package com.Tejas.TaskManagementSystem.Repository;

import com.Tejas.TaskManagementSystem.Entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace,Long> {
    Optional <Workspace> findByName(String name);
}
