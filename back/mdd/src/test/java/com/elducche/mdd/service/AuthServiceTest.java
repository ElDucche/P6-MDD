package com.elducche.mdd.service;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.LoginResponse;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.security.JwtUtil;
import com.elducche.mdd.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "tester";
    private static final String TEST_PASSWORD = "password";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtUtil jwtUtil; // instance réelle
    private AuthService authService; // construit manuellement

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        jwtUtil = createRealJwtUtil();
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtUtil);
    }

    private JwtUtil createRealJwtUtil() throws Exception {
        JwtUtil util = new JwtUtil();
        // Injection via réflexion des champs @Value
        Field secret = JwtUtil.class.getDeclaredField("jwtSecret");
        secret.setAccessible(true);
        secret.set(util, "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzSECRETKEY!!!123456"); // 64+ chars
        Field exp = JwtUtil.class.getDeclaredField("jwtExpiration");
        exp.setAccessible(true);
        exp.set(util, 3600000L);
        util.init();
        return util;
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

    @Test
    @DisplayName("login() succès via email")
    void loginSuccessByEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);
        user.setUsername(TEST_USERNAME);
        user.setPassword("ENCODED");

        LoginRequest req = new LoginRequest();
        req.setIdentifier(TEST_EMAIL);
        req.setPassword(TEST_PASSWORD);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, "ENCODED")).thenReturn(true);

        LoginResponse resp = authService.login(req);
        assertNotNull(resp.getToken());
        assertEquals("Connexion réussie", resp.getMessage());
        assertNotNull(resp.getUser());
        assertEquals(TEST_EMAIL, resp.getUser().getEmail());
    }

    @Test
    @DisplayName("login() succès via username")
    void loginSuccessByUsername() {
        User user = new User();
        user.setId(2L);
        user.setEmail(TEST_EMAIL);
        user.setUsername(TEST_USERNAME);
        user.setPassword("ENCODED");

        LoginRequest req = new LoginRequest();
        req.setIdentifier(TEST_USERNAME);
        req.setPassword(TEST_PASSWORD);

        when(userRepository.findByEmail(TEST_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmailOrUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TEST_PASSWORD, "ENCODED")).thenReturn(true);

        LoginResponse resp = authService.login(req);
        assertNotNull(resp.getToken());
        assertEquals("Connexion réussie", resp.getMessage());
        assertEquals(TEST_USERNAME, resp.getUser().getUsername());
    }

    @Test
    @DisplayName("login() échec identifiant inconnu")
    void loginFailUnknownIdentifier() {
        LoginRequest req = new LoginRequest();
        req.setIdentifier("inconnu");
        req.setPassword(TEST_PASSWORD);

        when(userRepository.findByEmail("inconnu")).thenReturn(Optional.empty());
        when(userRepository.findByEmailOrUsername("inconnu")).thenReturn(Optional.empty());

        LoginResponse resp = authService.login(req);
        assertNull(resp.getToken());
        assertEquals("Email ou mot de passe incorrect", resp.getMessage());
    }

    @Test
    @DisplayName("login() échec mauvais mot de passe")
    void loginFailBadPassword() {
        User user = new User();
        user.setId(3L);
        user.setEmail(TEST_EMAIL);
        user.setUsername(TEST_USERNAME);
        user.setPassword("ENCODED");

        LoginRequest req = new LoginRequest();
        req.setIdentifier(TEST_EMAIL);
        req.setPassword("WRONG");

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WRONG", "ENCODED")).thenReturn(false);

        LoginResponse resp = authService.login(req);
        assertNull(resp.getToken());
        assertEquals("Email ou mot de passe incorrect", resp.getMessage());
    }
}
