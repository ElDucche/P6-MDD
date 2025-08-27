package com.elducche.mdd.service;

import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private static final String TEST_EMAIL = "a@b.com";
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("bob");
        when(userRepository.save(any(User.class))).thenReturn(user);
        User created = userService.save(user);
        assertEquals("bob", created.getUsername());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("bob");
        when(userRepository.save(any(User.class))).thenReturn(user);
        User updated = userService.save(user);
        assertEquals(1L, updated.getId());
        assertEquals("bob", updated.getUsername());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setEmail(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(user));
        Optional<User> result = userService.findByEmail(TEST_EMAIL);
        assertTrue(result.isPresent());
        assertEquals(TEST_EMAIL, result.get().getEmail());
    }
}
