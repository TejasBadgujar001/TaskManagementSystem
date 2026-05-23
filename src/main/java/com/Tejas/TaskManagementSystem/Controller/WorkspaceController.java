package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.*;
import com.Tejas.TaskManagementSystem.Service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
    private final WorkspaceService service;

    //API for creating Workspace
    @PostMapping("/post")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody WorkspaceRequest request){
        WorkspaceResponse response = service.createWorkspace(request);
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API for adding users to workspace
    @PostMapping("/post/users/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> addUsersToWorkspace(@PathVariable Long id,@Valid @RequestBody List<Long>ids){
        return new ResponseEntity<>(service.addUserToWorkspace(id,ids),HttpStatus.OK);
    }

    //API For fetching all workspace
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    @GetMapping("/all")
    public ResponseEntity<List<WorkspaceResponse>> getAllWorkspace(){
        return new ResponseEntity<>(service.getAllWorkspace(),HttpStatus.OK);
    }
    //API For fetching workspace using Id
    @GetMapping("/id/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(@PathVariable Long id){
        return new ResponseEntity<>(service.getWorkspaceById(id),HttpStatus.OK);
    }
    //API For fetching workspace using name
    @GetMapping("/name/{name}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> getWorkspaceByName(@PathVariable String name){
        return new ResponseEntity<>(service.getWorkspaceByName(name),HttpStatus.OK);
    }

    //API for update the workspace
    @PatchMapping("/update/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<WorkspaceResponse> updateWorkspace(@PathVariable Long id,@Valid @RequestBody WorkspaceUpdateRequest request){
        return new ResponseEntity<>(service.updateWorkspace(id,request),HttpStatus.OK);
    }

    //API for delete workspace
    @DeleteMapping("/delete/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<String> deleteWorkspace(@PathVariable Long id){
        return new ResponseEntity<>(service.deleteWorkspace(id),HttpStatus.OK);
    }

    //API for fetching all users for workspace
    @GetMapping("/users/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<List<UserResponse>> getAllUsersForWorkspace(@PathVariable Long id){
        return new ResponseEntity<>(service.getAllUserOfWorkspace(id),HttpStatus.OK);
    }

    //API For fetching all tasks for workspace
    @GetMapping("/task/{id}")
    @PreAuthorize(("hasAnyRole('ADMIN','MANAGER')"))
    public ResponseEntity<List<TaskResponse>> getAllTasksForWorkspace(@PathVariable Long id){
        return new ResponseEntity<>(service.getAllTaskOfWorkspace(id),HttpStatus.OK);
    }

}
