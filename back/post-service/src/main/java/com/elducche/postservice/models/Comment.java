package com.elducche.postservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("COMMENTS")
public class Comment {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
