package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Theme;
import org.springframework.data.repository.CrudRepository;

public interface ThemeRepository extends CrudRepository<Theme, Long> {
}
