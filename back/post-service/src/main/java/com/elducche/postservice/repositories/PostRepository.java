package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByThemeId(Long themeId);
    List<Post> findByAuthorId(Long authorId);
}
