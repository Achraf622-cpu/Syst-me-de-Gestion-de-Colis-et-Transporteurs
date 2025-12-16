package org.example.colis.service;

import org.example.colis.dto.LoginRequest;
import org.example.colis.dto.LoginResponse;
import org.example.colis.enums.Role;
import org.example.colis.exception.UnauthorizedException;
import org.example.colis.model.User;
import org.example.colis.repository.UserRepository;
import org.example.colis.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setLogin("admin");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setRole(Role.ADMIN);
        testUser.setActive(true);
        
        loginRequest = new LoginRequest("admin", "admin123");
    }
    
    @Test
    void login_WithValidCredentials_ShouldReturnLoginResponse() {
        // Arrange
        when(userRepository.findByLoginAndActiveTrue("admin")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("admin123", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token");
        
        // Act
        LoginResponse response = authService.login(loginRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("admin", response.getLogin());
        assertEquals("ADMIN", response.getRole());
        
        verify(userRepository).findByLoginAndActiveTrue("admin");
        verify(passwordEncoder).matches("admin123", testUser.getPassword());
        verify(jwtUtil).generateToken(testUser);
    }
    
    @Test
    void login_WithInvalidPassword_ShouldThrowUnauthorizedException() {
        // Arrange
        when(userRepository.findByLoginAndActiveTrue("admin")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);
        
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.login(
                new LoginRequest("admin", "wrongPassword")
        ));
        
        verify(userRepository).findByLoginAndActiveTrue("admin");
        verify(passwordEncoder).matches("wrongPassword", testUser.getPassword());
        verify(jwtUtil, never()).generateToken(any());
    }
    
    @Test
    void login_WithNonExistentUser_ShouldThrowUnauthorizedException() {
        // Arrange
        when(userRepository.findByLoginAndActiveTrue("nonexistent")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.login(
                new LoginRequest("nonexistent", "password")
        ));
        
        verify(userRepository).findByLoginAndActiveTrue("nonexistent");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any());
    }
    
    @Test
    void login_WithInactiveUser_ShouldThrowUnauthorizedException() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findByLoginAndActiveTrue("admin")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
        
        verify(userRepository).findByLoginAndActiveTrue("admin");
    }
}
