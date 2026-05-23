package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.TaskRequest;
import com.Tejas.TaskManagementSystem.DTO.TaskResponse;
import com.Tejas.TaskManagementSystem.DTO.TaskUpdateRequest;
import com.Tejas.TaskManagementSystem.Service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService service;

    //API for posting task
    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TaskResponse> postTask(@Valid @RequestBody TaskRequest request){
        TaskResponse response= service.addTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //API for fetching tasks
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskResponse>> getAllTask(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size
    ){
        List<TaskResponse> responses = service.fetchAllTask(page, size);
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @GetMapping("/postedTask")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<TaskResponse>> getAllTaskPostByUser(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size
    ){
        List<TaskResponse> responses = service.getAllPostedTaskByUser(page, size);
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    //This API is for to check how many task assigned to us
    @GetMapping("/myTask")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<List<TaskResponse>> getAllTaskAssignedToUser(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5")int size
    ){
        List<TaskResponse> responses = service.getTaskForUser(page,size);
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MEMBER')")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId){
        TaskResponse response = service.getTaskById(taskId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    //API for update task
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,@Valid @RequestBody TaskUpdateRequest request){
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
