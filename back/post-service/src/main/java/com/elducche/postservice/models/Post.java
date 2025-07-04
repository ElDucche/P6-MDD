package com.elducche.postservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("POSTS")
public class Post {
    @Id
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Long themeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
