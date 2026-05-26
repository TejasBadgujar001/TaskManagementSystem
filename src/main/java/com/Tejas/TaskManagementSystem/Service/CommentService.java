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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles business logic for task comments including:
    - comment creation
    - ownership-based updates/deletion
    - comment retrieval
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public CommentResponse addComment(Long id, CommentRequest request){
        Comment comment = mapToEntity(request);
        TaskEntity entity =  taskService.getTaskEntityById(id);
        comment.setTaskEntity(entity);
        comment = commentRepository.save(comment);
        logger.info("Comment is added for task with id: {}", id);
        return mapToResponse(comment);
    }

    // Only comment owner is allowed to update comment
    public CommentResponse updateComment(Long id,CommentRequest request){
        UserEntity entity = userService.getLoggedInUserEntity();
        logger.info("Attempting to edit the comment with id: {}",id);
        Comment comment= commentRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Comment not found for id: {}", id);
                    return new ResourceNotFoundException("No Comment with id: "+id);
                });
        if(entity.getId().equals(comment.getUserEntity().getId())){
            comment.setContent(request.getContent());
            commentRepository.save(comment);
            logger.info("Comment edited successfully for id: {}", id);
            return mapToResponse(comment);
        }else {
            logger.warn("Unauthorized profile attempting to edit the comment for id: {}", id);
            throw new UnauthorizedException("You are not allowed to update comment with id: "+id);
        }
    }

    public List<CommentResponse> getAllCommentsForTask(Long id,int page,int size){
        Pageable pageable = PageRequest.of(page,size);
        TaskResponse response = taskService.getTaskById(id);
        logger.info("Fetching all comments for task with id: {}", id);
        return commentRepository.findByTaskEntityId(id,pageable)
                .stream().map(comment-> mapToResponse(comment))
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getAllCommentsForUser(Long id,int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        UserEntity user = userService.getLoggedInUserEntity();
        if(user.getId().equals(id)){
            logger.info("Fetching all comments done by user for id: {}", id);
            return commentRepository.findByUserEntityId(id,pageable)
                    .stream().map(comment-> mapToResponse(comment))
                    .collect(Collectors.toList());
        }else {
            logger.warn("Unauthorized profile attempting to fetch comments for user with id: {}",id);
            throw new UnauthorizedException("You are not allowed to see comment of user with id: "+id);
        }
    }

    public String deleteComment(Long id){
        logger.info("Attempting to delete comment for id: {}", id);
        UserEntity entity = userService.getLoggedInUserEntity();
        Comment comment= commentRepository.findById(id)
                .orElseThrow(()->{
                    logger.warn("Comment not found for id: {}", id);
                    return new ResourceNotFoundException("No Comment with id: "+id);
                });
        if(entity.getId().equals(comment.getUserEntity().getId())){
            commentRepository.deleteById(id);
            logger.info("Comment deleted successfully for id: {}",id);
            return "Comment deleted Successfully";
        }else {
            logger.warn("Unauthorized profile attempting to delete comments with id: {}",id);
            throw new UnauthorizedException("You are not allowed to delete comment with id: "+id);
        }
    }

    public CommentResponse mapToResponse(Comment comment){
        return CommentResponse.builder()
                .content(comment.getContent())
                .taskName(comment.getTaskEntity().getTitle())
                .userName(comment.getUserEntity().getName())
                .userEmail(comment.getUserEntity().getEmail())
                .build();
    }
    public Comment mapToEntity(CommentRequest request){
        UserEntity entity = userService.getLoggedInUserEntity();
        return  Comment.builder()
                .content(request.getContent())
                .userEntity(entity)
                .build();

    }
}
