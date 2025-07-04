package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Post;
import com.elducche.postservice.services.PostService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Flux<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/theme/{themeId}")
    public Flux<Post> getPostsByTheme(@PathVariable Long themeId) {
        return postService.getPostsByTheme(themeId);
    }

    @PostMapping
    public Mono<Post> createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }
}
