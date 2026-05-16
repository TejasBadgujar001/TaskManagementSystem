package com.Tejas.TaskManagementSystem.Repository;

import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findBYEmail(String email);
}
