package com.elducche.postservice.services;

import com.elducche.postservice.models.Comment;
import com.elducche.postservice.repositories.CommentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Mono<Comment> createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Flux<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }
}
