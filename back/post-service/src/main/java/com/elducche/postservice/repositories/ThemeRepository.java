package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Theme;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ThemeRepository extends ReactiveCrudRepository<Theme, Long> {
}
