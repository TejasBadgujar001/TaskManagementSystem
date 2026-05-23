package com.Tejas.TaskManagementSystem.Controller;

import com.Tejas.TaskManagementSystem.DTO.CommentRequest;
import com.Tejas.TaskManagementSystem.DTO.CommentResponse;
import com.Tejas.TaskManagementSystem.Service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    //API for posting comments
    @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
    @PostMapping("/post/{id}")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long id, @Valid @RequestBody CommentRequest request){
        CommentResponse response = service.addComment(id,request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //API for updating comment
    @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id,@Valid @RequestBody CommentRequest request){
        CommentResponse response = service.updateComment(id,request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API for deleting comment
    @PreAuthorize("hasAnyRole('MANAGER','MEMBER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id){
        String response = service.deleteComment(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API for fetching all comments for task
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/task/{id}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForTask(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return  new ResponseEntity<>(service.getAllCommentsForTask(id,page,size),HttpStatus.OK);
    }
    //API for fetching all comments for user
    @PreAuthorize("hasAnyRole('MEMBER','MANAGER')")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<CommentResponse>> getAllCommentsForUser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "5") int size
    ){
        return  new ResponseEntity<>(service.getAllCommentsForUser(id,page,size),HttpStatus.OK);
    }

}
