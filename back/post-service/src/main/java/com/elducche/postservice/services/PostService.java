package com.elducche.postservice.services;

import com.elducche.postservice.models.Post;
import com.elducche.postservice.repositories.PostRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return (List<Post>) postRepository.findAll();
    }

    public List<Post> getPostsByTheme(Long themeId) {
        return postRepository.findByThemeId(themeId);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }
}
