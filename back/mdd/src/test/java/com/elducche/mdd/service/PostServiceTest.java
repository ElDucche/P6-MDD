package com.elducche.mdd.service;

import com.elducche.mdd.entity.Post;
import com.elducche.mdd.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {
    private static final String TEST_TITLE = "Titre";
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Post post = new Post();
        post.setId(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Optional<Post> result = postService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testCreatePost() {
        Post post = new Post();
        post.setTitle(TEST_TITLE);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        Post created = postService.save(post);
        assertEquals(TEST_TITLE, created.getTitle());
    }

    @Test
    void testUpdatePost() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle(TEST_TITLE);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        Post updated = postService.save(post);
        assertEquals(1L, updated.getId());
        assertEquals(TEST_TITLE, updated.getTitle());
    }

    @Test
    void testDeletePost() {
        doNothing().when(postRepository).deleteById(1L);
        postService.deleteById(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }
}
