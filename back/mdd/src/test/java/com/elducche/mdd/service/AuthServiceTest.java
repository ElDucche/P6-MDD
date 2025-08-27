package com.elducche.mdd.service;

import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password";
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setEmail(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        Optional<User> result = authService.findByEmail(TEST_EMAIL);
        assertTrue(result.isPresent());
        assertEquals(TEST_EMAIL, result.get().getEmail());
    }

    @Test
    void testAuthenticateSuccess() {
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, TEST_PASSWORD)).thenReturn(true);
        Optional<User> result = authService.authenticate(TEST_EMAIL, TEST_PASSWORD);
        assertTrue(result.isPresent());
        assertEquals(TEST_EMAIL, result.get().getEmail());
    }

    @Test
    void testAuthenticateFail() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        Optional<User> result = authService.authenticate(TEST_EMAIL, TEST_PASSWORD);
        assertFalse(result.isPresent());
    }
}
