package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Comment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveCrudRepository<Comment, Long> {
    Flux<Comment> findByPostId(Long postId);
}
