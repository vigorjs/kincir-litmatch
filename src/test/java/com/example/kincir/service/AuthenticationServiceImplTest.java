package com.example.kincir.service;

import com.example.kincir.config.security.JwtUtils;
import com.example.kincir.config.security.advisers.exception.NotFoundException;
import com.example.kincir.model.enums.UserRole;
import com.example.kincir.model.meta.Subscription;
import com.example.kincir.model.meta.SubscriptionPlan;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.SubscriptionPlanRepository;
import com.example.kincir.repository.SubscriptionRepository;
import com.example.kincir.repository.UserRepository;
import com.example.kincir.service.impl.AuthenticationServiceImpl;
import com.example.kincir.utils.dto.request.AuthenticationRequestDTO;
import com.example.kincir.utils.dto.request.RegisterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private SubscriptionPlanRepository subscriptionPlanRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegisterRequestDTO registerRequest;
    private AuthenticationRequestDTO loginRequest;
    private User savedUser;
    private SubscriptionPlan subscriptionPlanFree;
    private SubscriptionPlan subscriptionPlanLifeTime;
    private final String VALID_USERNAME = "testuser";
    private final String VALID_EMAIL = "test@example.com";
    private final String VALID_PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encodedPassword123";
    private final String JWT_TOKEN = "jwtToken123";
    private final String REFRESH_TOKEN = "refreshToken123";

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO(VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD);
        loginRequest = new AuthenticationRequestDTO(VALID_EMAIL, VALID_PASSWORD);

        savedUser = User.builder()
                .id(1)
                .username(VALID_USERNAME)
                .email(VALID_EMAIL)
                .password(ENCODED_PASSWORD)
                .role(UserRole.USER)
                .build();

        subscriptionPlanFree = SubscriptionPlan.builder()
                .id(UUID.randomUUID())
                .name("FREE TRIAL")
                .price(0.0)
                .description("Free Trial 1 Week")
                .isLifetime(false)
                .duration(604800)
                .build();

        subscriptionPlanLifeTime = SubscriptionPlan.builder()
                .id(UUID.randomUUID())
                .name("LIFETIME")
                .price(0.0)
                .description("Lifetime Subscription")
                .isLifetime(true)
                .duration(999999999)
                .build();
    }

    @Test
    void register_WithValidRequest_ShouldSucceed() {
        // Arrange
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(JWT_TOKEN);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(REFRESH_TOKEN);
        when(subscriptionPlanRepository.findByName("FREE TRIAL")).thenReturn(Optional.of(subscriptionPlanFree));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(any(Subscription.class));

        // Act
        var result = authenticationService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(JWT_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(savedUser, result.getUser());

        verify(passwordEncoder).encode(VALID_PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
        verify(subscriptionPlanRepository).findByName("FREE TRIAL");
        verify(subscriptionRepository).save(any(Subscription.class));
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
        when(subscriptionPlanRepository.findByName("LIFETIME")).thenReturn(Optional.of(subscriptionPlanLifeTime));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(any(Subscription.class));

        // Act
        var result = authenticationService.register(registerRequest, adminToken);

        // Assert
        assertNotNull(result);
        assertEquals(JWT_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(adminUser, result.getUser());
        assertEquals(UserRole.ADMIN, result.getUser().getRole());

        verify(subscriptionPlanRepository).findByName("LIFETIME");
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void authenticate_WithValidCredentials_ShouldSucceed() {
        // Arrange
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(any(User.class))).thenReturn(JWT_TOKEN);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(REFRESH_TOKEN);

        // Act
        var result = authenticationService.authenticate(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(JWT_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(savedUser, result.getUser());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(VALID_EMAIL, VALID_PASSWORD)
        );
    }

//    @Test
//    void refreshToken_WithValidToken_ShouldSucceed() throws IOException {
//        // Arrange
//        String validRefreshToken = "Bearer " + REFRESH_TOKEN;
//        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(validRefreshToken);
//        when(jwtService.extractUsername(REFRESH_TOKEN)).thenReturn(VALID_EMAIL);
//        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(savedUser));
//        when(jwtService.isTokenValid(REFRESH_TOKEN, savedUser)).thenReturn(true);
//        when(jwtService.generateToken(savedUser)).thenReturn(JWT_TOKEN);
//
//        // Act
//        authenticationService.refreshToken(request, response);
//
//        // Assert
//        verify(jwtService).extractUsername(REFRESH_TOKEN);
//        verify(userRepository).findByEmail(VALID_EMAIL);
//        verify(jwtService).isTokenValid(REFRESH_TOKEN, savedUser);
//        verify(jwtService).generateToken(savedUser);
//    }

    @Test
    void getUserAuthenticated_ShouldReturnUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(VALID_EMAIL);
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(savedUser));

        // Act
        User result = authenticationService.getUserAuthenticated();

        // Assert
        assertNotNull(result);
        assertEquals(savedUser, result);
        verify(userRepository).findByEmail(VALID_EMAIL);
    }

    @Test
    void getUserAuthenticated_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(VALID_EMAIL);
        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> authenticationService.getUserAuthenticated());
        verify(userRepository).findByEmail(VALID_EMAIL);
    }

    @Test
    void register_WhenSubscriptionPlanNotFound_ShouldThrowException() {
        // Arrange
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(subscriptionPlanRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> authenticationService.register(registerRequest));
        verify(subscriptionPlanRepository).findByName("FREE TRIAL");
    }
}