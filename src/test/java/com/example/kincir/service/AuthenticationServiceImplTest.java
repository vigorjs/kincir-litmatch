package com.example.kincir.service;

import com.example.kincir.config.security.JwtUtils;
import com.example.kincir.model.enums.UserRole;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.UserRepository;
import com.example.kincir.service.impl.AuthenticationServiceImpl;
import com.example.kincir.utils.dto.request.AuthenticationRequestDTO;
import com.example.kincir.utils.dto.request.RegisterRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegisterRequestDTO registerRequest;
    private AuthenticationRequestDTO loginRequest;
    private User savedUser;
    private final String VALID_USERNAME = "testuser";
    private final String VALID_EMAIL = "test@example.com";
    private final String VALID_PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encodedPassword123";
    private final String JWT_TOKEN = "jwtToken123";
    private final String REFRESH_TOKEN = "refreshToken123";

    @BeforeEach
    void setUp() {
        // Setup valid request
        registerRequest = new RegisterRequestDTO(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        loginRequest = new AuthenticationRequestDTO(VALID_EMAIL, ENCODED_PASSWORD);

        // Setup saved user
        savedUser = User.builder()
                .id(1)
                .username(VALID_USERNAME)
                .email(VALID_EMAIL)
                .password(ENCODED_PASSWORD)
                .role(UserRole.USER)
                .build();
    }

    @Test
    void register_WithValidRequest_ShouldSucceed() {
        // Arrange
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(JWT_TOKEN);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(REFRESH_TOKEN);

        // Act
        var result = authenticationService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(JWT_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(savedUser, result.getUser());

        // Verify interactions
        verify(passwordEncoder).encode(VALID_PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
    }

    @Test
    void register_WithAdminRole_ShouldSucceed() {
        // Arrange
        String adminToken = "validAdminToken";
        User adminUser = User.builder()
                .id(1)
                .username(VALID_USERNAME)
                .email(VALID_EMAIL)
                .password(ENCODED_PASSWORD)
                .role(UserRole.ADMIN)
                .build();

        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(JWT_TOKEN);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(REFRESH_TOKEN);

        // Act
        var result = authenticationService.register(registerRequest, adminToken);

        // Assert
        assertNotNull(result);
        assertEquals(JWT_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(adminUser, result.getUser());
        assertEquals(UserRole.ADMIN, result.getUser().getRole());

        // Verify interactions
        verify(passwordEncoder).encode(VALID_PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
    }

    @Test
    void login_ShouldSucceed() {
        // Arrange
        User user = User.builder()
                .id(1)
                .username(VALID_USERNAME)
                .email(VALID_EMAIL)
                .password(ENCODED_PASSWORD)
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn(JWT_TOKEN);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(REFRESH_TOKEN);

        // Act
        var result = authenticationService.authenticate(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(JWT_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(user, result.getUser());
        assertEquals(UserRole.USER, result.getUser().getRole());

        // Verify interactions
        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
    }
}