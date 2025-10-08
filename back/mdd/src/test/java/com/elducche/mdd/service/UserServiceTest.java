package com.elducche.mdd.service;

import com.elducche.mdd.dto.UpdateUserProfileRequest;
import com.elducche.mdd.dto.UserResponse;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.exception.ResourceNotFoundException;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private static final String TEST_EMAIL = "a@b.com";
    private static final String TEST_USERNAME = "bob";
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

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
        user.setUsername(TEST_USERNAME);
        when(userRepository.save(any(User.class))).thenReturn(user);
        User created = userService.save(user);
        assertEquals(TEST_USERNAME, created.getUsername());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername(TEST_USERNAME);
        when(userRepository.save(any(User.class))).thenReturn(user);
        User updated = userService.save(user);
        assertEquals(1L, updated.getId());
        assertEquals(TEST_USERNAME, updated.getUsername());
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
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findByEmail(TEST_EMAIL);
        assertTrue(result.isPresent());
        assertEquals(TEST_EMAIL, result.get().getEmail());
    }
    
    @Test
    void testFindByUsername() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findByUsername(TEST_USERNAME);
        assertTrue(result.isPresent());
        assertEquals(TEST_USERNAME, result.get().getUsername());
    }
    
    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        List<User> users = userService.findAll();
        
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void testGetUserProfile() {
        User user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);
        user.setUsername(TEST_USERNAME);
        
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        
        UserResponse response = userService.getUserProfile(TEST_EMAIL);
        
        assertEquals(1L, response.getId());
        assertEquals(TEST_EMAIL, response.getEmail());
        assertEquals(TEST_USERNAME, response.getUsername());
    }
    
    @Test
    void testGetUserProfileNotFound() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserProfile(TEST_EMAIL);
        });
    }
    
    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);
        user.setUsername(TEST_USERNAME);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        UserResponse response = userService.getUserById(1L);
        
        assertEquals(1L, response.getId());
        assertEquals(TEST_EMAIL, response.getEmail());
    }
    
    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }
    
    @Test
    void testUpdateUserProfileWithUsername() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setUsername("oldusername");
        existingUser.setPassword("encodedPassword");
        
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setUsername("newusername");
        
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        UserResponse response = userService.updateUserProfile(TEST_EMAIL, request);
        
        assertNotNull(response);
        verify(userRepository, times(1)).existsByUsername("newusername");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testUpdateUserProfileWithDuplicateUsername() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setUsername("oldusername");
        
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setUsername("existingusername");
        
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("existingusername")).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserProfile(TEST_EMAIL, request);
        });
    }
    
    @Test
    void testUpdateUserProfileWithEmail() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setUsername(TEST_USERNAME);
        
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setEmail("newemail@test.com");
        
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("newemail@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        UserResponse response = userService.updateUserProfile(TEST_EMAIL, request);
        
        assertNotNull(response);
        verify(userRepository, times(1)).existsByEmail("newemail@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testUpdateUserProfileWithPassword() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setUsername(TEST_USERNAME);
        
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setPassword("newPassword123");
        
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        UserResponse response = userService.updateUserProfile(TEST_EMAIL, request);
        
        assertNotNull(response);
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testChangePasswordSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        boolean result = userService.changePassword(1L, "oldPassword", "newPassword");
        
        assertTrue(result);
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedOldPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testChangePasswordWithWrongCurrentPassword() {
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);
        
        boolean result = userService.changePassword(1L, "wrongPassword", "newPassword");
        
        assertFalse(result);
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedOldPassword");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testChangePasswordForNonExistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        boolean result = userService.changePassword(999L, "oldPassword", "newPassword");
        
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void testDeleteUserSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        
        boolean result = userService.deleteUser(1L);
        
        assertTrue(result);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteNonExistentUser() {
        when(userRepository.existsById(999L)).thenReturn(false);
        
        boolean result = userService.deleteUser(999L);
        
        assertFalse(result);
        verify(userRepository, never()).deleteById(999L);
    }
    
    @Test
    void testCheckPasswordMatches() {
        User user = new User();
        user.setPassword("encodedPassword");
        
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        
        boolean result = userService.checkPassword(user, "rawPassword");
        
        assertTrue(result);
        verify(passwordEncoder, times(1)).matches("rawPassword", "encodedPassword");
    }
    
    @Test
    void testCheckPasswordDoesNotMatch() {
        User user = new User();
        user.setPassword("encodedPassword");
        
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
        
        boolean result = userService.checkPassword(user, "wrongPassword");
        
        assertFalse(result);
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
    }
    
    @Test
    void testUpdateUserWithNewUsername() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldusername");
        existingUser.setEmail(TEST_EMAIL);
        
        User updates = new User();
        updates.setUsername("newusername");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        
        Optional<User> result = userService.updateUser(1L, updates);
        
        assertTrue(result.isPresent());
        verify(userRepository, times(1)).existsByUsername("newusername");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testUpdateUserWithDuplicateEmail() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail(TEST_EMAIL);
        
        User updates = new User();
        updates.setEmail("existing@test.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(1L, updates);
        });
    }
}
