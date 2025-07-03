package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PostRepository extends ReactiveCrudRepository<Post, Long> {
    Flux<Post> findByThemeId(Long themeId);
}
