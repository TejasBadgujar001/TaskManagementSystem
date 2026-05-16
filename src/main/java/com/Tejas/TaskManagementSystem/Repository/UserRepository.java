package com.Tejas.TaskManagementSystem.Repository;

import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

}
