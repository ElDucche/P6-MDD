package com.elducche.postservice.services;

import com.elducche.postservice.models.Post;
import com.elducche.postservice.repositories.PostRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Flux<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Flux<Post> getPostsByTheme(Long themeId) {
        return postRepository.findByThemeId(themeId);
    }

    public Mono<Post> createPost(Post post) {
        return postRepository.save(post);
    }
}
