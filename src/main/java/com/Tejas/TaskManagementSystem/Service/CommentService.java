package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.DTO.CommentRequest;
import com.Tejas.TaskManagementSystem.DTO.CommentResponse;
import com.Tejas.TaskManagementSystem.DTO.TaskResponse;
import com.Tejas.TaskManagementSystem.Entity.Comment;
import com.Tejas.TaskManagementSystem.Entity.TaskEntity;
import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Exception.ResourceNotFoundException;
import com.Tejas.TaskManagementSystem.Exception.UnauthorizedException;
import com.Tejas.TaskManagementSystem.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;

    //Method for posting comment
    public CommentResponse addComment(Long id, CommentRequest request){
        Comment comment = toEntity(request);
        TaskEntity entity =  taskService.getTaskEntityById(id);
        comment.setTaskEntity(entity);
        comment = commentRepository.save(comment);
        return toResponse(comment);
    }

    //Method for updating comment
    public CommentResponse updateComment(Long id,CommentRequest request){
        UserEntity entity = userService.getLoggedInUserEntity();
        Comment comment= commentRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No Comment with id: "+id));
        if(entity.getId().equals(comment.getUserEntity().getId())){
            comment.setContent(request.getContent());
            commentRepository.save(comment);
            return toResponse(comment);
        }else {
            throw new UnauthorizedException("You are not allowed to update comment with id: "+id);
        }
    }

    //Get all comment for task
    public List<CommentResponse> getAllCommentsForTask(Long id){
        TaskResponse response = taskService.getTaskById(id);
        return commentRepository.findByTaskEntityId(id)
                .stream().map(comment->toResponse(comment))
                .collect(Collectors.toList());
    }
    //Get all comment done by user
    public List<CommentResponse> getAllCommentsForUser(Long id){
        UserEntity user = userService.getLoggedInUserEntity();
        if(user.getId().equals(id)){
            return commentRepository.findByUserEntityId(id)
                    .stream().map(comment->toResponse(comment))
                    .collect(Collectors.toList());
        }else {
            throw new UnauthorizedException("You are not allowed to see comment of user with id: "+id);
        }
    }

    //Method for deleting comment
    public String deleteComment(Long id){
        UserEntity entity = userService.getLoggedInUserEntity();
        Comment comment= commentRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No Comment with id: "+id));
        if(entity.getId().equals(comment.getUserEntity().getId())){
            commentRepository.deleteById(id);
            return "Comment deleted Successfully";
        }else {
            throw new UnauthorizedException("You are not allowed to delete comment with id: "+id);
        }
    }

    //Helper methods
    public CommentResponse toResponse(Comment comment){
        return CommentResponse.builder()
                .content(comment.getContent())
                .taskName(comment.getTaskEntity().getTitle())
                .userName(comment.getUserEntity().getName())
                .userEmail(comment.getUserEntity().getEmail())
                .build();
    }
    public Comment toEntity(CommentRequest request){
        UserEntity entity = userService.getLoggedInUserEntity();
        return  Comment.builder()
                .content(request.getContent())
                .userEntity(entity)
                .build();

    }
}
