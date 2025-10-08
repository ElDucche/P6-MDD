package com.elducche.mdd.service;

import com.elducche.mdd.dto.CommentCreateRequest;
import com.elducche.mdd.entity.Comment;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.CommentRepository;
import com.elducche.mdd.repository.PostRepository;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    private static final String TEST_CONTENT = "Bravo !";
    
    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private PostRepository postRepository;
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

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
    
    @Test
    void testGetCommentsByPost() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        
        when(commentRepository.findByPostIdWithAuthor(1L)).thenReturn(Arrays.asList(comment1, comment2));
        
        List<Comment> comments = commentService.getCommentsByPost(1L);
        
        assertEquals(2, comments.size());
        verify(commentRepository, times(1)).findByPostIdWithAuthor(1L);
    }
    
    @Test
    void testGetCommentsByPostId() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        
        when(commentRepository.findByPostIdWithAuthor(1L)).thenReturn(Arrays.asList(comment1));
        
        List<Comment> comments = commentService.getCommentsByPostId(1L);
        
        assertEquals(1, comments.size());
        verify(commentRepository, times(1)).findByPostIdWithAuthor(1L);
    }
    
    @Test
    void testGetCommentsByUser() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        
        when(commentRepository.findByAuthorIdWithPostAndTheme(1L)).thenReturn(Arrays.asList(comment1));
        
        List<Comment> comments = commentService.getCommentsByUser(1L);
        
        assertEquals(1, comments.size());
        verify(commentRepository, times(1)).findByAuthorIdWithPostAndTheme(1L);
    }
    
    @Test
    void testCountCommentsByPost() {
        when(commentRepository.countByPostId(1L)).thenReturn(5L);
        
        long count = commentService.countCommentsByPost(1L);
        
        assertEquals(5L, count);
        verify(commentRepository, times(1)).countByPostId(1L);
    }
    
    @Test
    void testCreateCommentSuccessfully() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setPostId(1L);
        request.setContent("Great post!");
        
        User author = new User();
        author.setId(1L);
        
        Post post = new Post();
        post.setId(1L);
        
        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent("Great post!");
        savedComment.setAuthor(author);
        savedComment.setPost(post);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(savedComment));
        
        Optional<Comment> result = commentService.createComment(request, 1L);
        
        assertTrue(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    void testCreateCommentWithNonExistentAuthor() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setPostId(1L);
        request.setContent("Great post!");
        
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Comment> result = commentService.createComment(request, 999L);
        
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    void testCreateCommentWithNonExistentPost() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setPostId(999L);
        request.setContent("Great post!");
        
        User author = new User();
        author.setId(1L);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Comment> result = commentService.createComment(request, 1L);
        
        assertFalse(result.isPresent());
        verify(postRepository, times(1)).findById(999L);
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    void testUpdateCommentSuccessfully() {
        User author = new User();
        author.setId(1L);
        
        Comment existingComment = new Comment();
        existingComment.setId(1L);
        existingComment.setContent("Old content");
        existingComment.setAuthor(author);
        
        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setContent("New content");
        updatedComment.setAuthor(author);
        
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(updatedComment));
        
        Optional<Comment> result = commentService.updateComment(1L, "New content", 1L);
        
        assertTrue(result.isPresent());
        verify(commentRepository, times(2)).findByIdWithAuthorAndPost(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    void testUpdateCommentUnauthorized() {
        User author = new User();
        author.setId(1L);
        
        Comment existingComment = new Comment();
        existingComment.setId(1L);
        existingComment.setAuthor(author);
        
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(existingComment));
        
        Optional<Comment> result = commentService.updateComment(1L, "New content", 999L);
        
        assertFalse(result.isPresent());
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    void testDeleteCommentSuccessfully() {
        User author = new User();
        author.setId(1L);
        
        Comment existingComment = new Comment();
        existingComment.setId(1L);
        existingComment.setAuthor(author);
        
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(existingComment));
        doNothing().when(commentRepository).delete(existingComment);
        
        boolean result = commentService.deleteComment(1L, 1L);
        
        assertTrue(result);
        verify(commentRepository, times(1)).delete(existingComment);
    }
    
    @Test
    void testDeleteCommentUnauthorized() {
        User author = new User();
        author.setId(1L);
        
        Comment existingComment = new Comment();
        existingComment.setId(1L);
        existingComment.setAuthor(author);
        
        when(commentRepository.findByIdWithAuthorAndPost(1L)).thenReturn(Optional.of(existingComment));
        
        boolean result = commentService.deleteComment(1L, 999L);
        
        assertFalse(result);
        verify(commentRepository, never()).delete(any(Comment.class));
    }
    
    @Test
    void testDeleteCommentsByPost() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        
        List<Comment> comments = Arrays.asList(comment1, comment2);
        
        when(commentRepository.findByPostId(1L)).thenReturn(comments);
        doNothing().when(commentRepository).deleteAll(comments);
        
        commentService.deleteCommentsByPost(1L);
        
        verify(commentRepository, times(1)).findByPostId(1L);
        verify(commentRepository, times(1)).deleteAll(comments);
    }
    
    @Test
    void testIsCommentOwner() {
        User author = new User();
        author.setId(1L);
        
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(author);
        
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        
        boolean isOwner = commentService.isCommentOwner(1L, 1L);
        
        assertTrue(isOwner);
        verify(commentRepository, times(1)).findById(1L);
    }
    
    @Test
    void testIsNotCommentOwner() {
        User author = new User();
        author.setId(1L);
        
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(author);
        
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        
        boolean isOwner = commentService.isCommentOwner(1L, 999L);
        
        assertFalse(isOwner);
        verify(commentRepository, times(1)).findById(1L);
    }
}
