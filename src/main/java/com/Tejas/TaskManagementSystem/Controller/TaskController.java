package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.TaskRequest;
import com.Tejas.TaskManagementSystem.DTO.TaskResponse;
import com.Tejas.TaskManagementSystem.Service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService service;

    //API for posting task
    @PostMapping("/post")
    public ResponseEntity<TaskResponse> postTask(@RequestBody TaskRequest request){
        TaskResponse response= service.addTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //API for fetching tasks
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTask(){
        List<TaskResponse> responses = service.fetchAllTask();
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @GetMapping("/post")
    public ResponseEntity<List<TaskResponse>> getAllTaskPostByUser(){
        List<TaskResponse> responses = service.getAllPostedTaskbyUser();
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TaskResponse>> getAllTaskAssignedToUser(){
        List<TaskResponse> responses = service.getTaskForUser();
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId){
        TaskResponse response = service.getTaskById(taskId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    //API for update task
    @PutMapping("/update/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest request){
        TaskResponse response = service.updateTask(id,request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //API for delete task
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id){
        String str = service.deleteTask(id);
        return new ResponseEntity<>(str,HttpStatus.OK);
    }
}
