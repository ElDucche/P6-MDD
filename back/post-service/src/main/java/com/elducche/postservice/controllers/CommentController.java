package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Comment;
import com.elducche.postservice.services.CommentService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Mono<Comment> createComment(@RequestBody Comment comment) {
        return commentService.createComment(comment);
    }

    @GetMapping("/post/{postId}")
    public Flux<Comment> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }
}
