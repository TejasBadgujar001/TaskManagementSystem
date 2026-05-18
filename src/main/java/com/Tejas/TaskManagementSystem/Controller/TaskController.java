package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.AuthDto;
import com.Tejas.TaskManagementSystem.DTO.TaskRequest;
import com.Tejas.TaskManagementSystem.DTO.TaskResponse;
import com.Tejas.TaskManagementSystem.DTO.UserResponse;
import com.Tejas.TaskManagementSystem.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService service;

    //API for posting task
    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TaskResponse> postTask(@RequestBody TaskRequest request){
        TaskResponse response= service.addTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //API for fetching tasks
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskResponse>> getAllTask(){
        List<TaskResponse> responses = service.fetchAllTask();
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @GetMapping("/postedTask")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<TaskResponse>> getAllTaskPostByUser(){
        List<TaskResponse> responses = service.getAllPostedTaskByUser();
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    //This API is for to check how many task assigned to us
    @GetMapping("/myTask")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<List<TaskResponse>> getAllTaskAssignedToUser(){
        List<TaskResponse> responses = service.getTaskForUser();
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId){
        TaskResponse response = service.getTaskById(taskId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    //API for update task
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest request){
        TaskResponse response = service.updateTask(id,request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for delete task
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        String str = service.deleteTask(id);
        return new ResponseEntity<>(str,HttpStatus.OK);
    }
}
