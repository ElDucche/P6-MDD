package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Post;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByThemeId(Long themeId);
}
