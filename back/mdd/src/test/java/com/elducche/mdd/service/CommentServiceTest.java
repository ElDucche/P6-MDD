package com.elducche.mdd.service;

import com.elducche.mdd.entity.Comment;
import com.elducche.mdd.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    private static final String TEST_CONTENT = "Bravo !";
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Comment comment = new Comment();
        comment.setId(1L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        Optional<Comment> result = commentService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testCreateComment() {
        Comment comment = new Comment();
        comment.setContent(TEST_CONTENT);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Comment created = commentService.save(comment);
        assertEquals(TEST_CONTENT, created.getContent());
    }

    @Test
    void testUpdateComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent(TEST_CONTENT);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Comment updated = commentService.save(comment);
        assertEquals(1L, updated.getId());
        assertEquals(TEST_CONTENT, updated.getContent());
    }

    @Test
    void testDeleteComment() {
        doNothing().when(commentRepository).deleteById(1L);
        commentService.deleteById(1L);
        verify(commentRepository, times(1)).deleteById(1L);
    }
}
