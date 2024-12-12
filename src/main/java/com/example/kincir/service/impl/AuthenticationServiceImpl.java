package com.example.kincir.service.impl;

import com.example.kincir.config.security.JwtUtils;
import com.example.kincir.config.security.advisers.exception.NotFoundException;
import com.example.kincir.model.enums.SubscriptionStatus;
import com.example.kincir.model.enums.UserRole;
import com.example.kincir.model.meta.Subscription;
import com.example.kincir.model.meta.SubscriptionPlan;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.SubscriptionPlanRepository;
import com.example.kincir.repository.SubscriptionRepository;
import com.example.kincir.repository.UserRepository;
import com.example.kincir.service.AuthenticationService;
import com.example.kincir.service.SubscriptionService;
import com.example.kincir.utils.dto.request.AuthenticationRequestDTO;
import com.example.kincir.utils.dto.request.RegisterRequestDTO;
import com.example.kincir.utils.dto.request.SubscriptionRequestDTO;
import com.example.kincir.utils.dto.response.AuthenticationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtils jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private final SubscriptionService subscriptionService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        return registerUser(request, UserRole.USER, null);
    }

    @Override
    public AuthenticationResponseDTO register(RegisterRequestDTO request, String adminToken) {
        // Validasi adminToken jika diperlukan
        return registerUser(request, UserRole.ADMIN, adminToken);
    }

    private AuthenticationResponseDTO registerUser(RegisterRequestDTO request, UserRole role, String adminToken) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        var savedUser = userRepository.save(user);

        SubscriptionPlan plan = subscriptionPlanRepository.findByName(savedUser.getRole().equals(UserRole.USER) ? "FREE TRIAL" : "LIFETIME").orElseThrow(() -> new NotFoundException("Plan NotFOund"));
        Long now = new Date().getTime();
        Subscription newSubscription = Subscription.builder()
                .user(savedUser)
                .plan(plan)
                .startDate(now)
                .endDate(now + plan.getDuration())
                .status(SubscriptionStatus.ACTIVE)
                .build();
        subscriptionRepository.save(newSubscription);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(savedUser)
                .build();
    }


    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }

//    if ("oauth2".equals(request.getPassword())) {
//        var user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow();
//        var jwtToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
//        return AuthenticationResponseDTO.builder()
//                .accessToken(jwtToken)
//                .refreshToken(refreshToken)
//                .user(user)
//                .build();
//    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                var authResponse = AuthenticationResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public User getUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new NotFoundException("Unauthorized, not found email hehehe"));
        return user;
    }
}
