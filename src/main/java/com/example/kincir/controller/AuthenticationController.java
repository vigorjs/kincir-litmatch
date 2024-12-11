package com.example.kincir.controller;

import com.example.kincir.config.security.JwtUtils;
import com.example.kincir.config.security.advisers.exception.NotFoundException;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.UserRepository;
import com.example.kincir.service.AuthenticationService;
import com.example.kincir.utils.dto.request.AuthenticationRequestDTO;
import com.example.kincir.utils.dto.request.RegisterRequestDTO;
import com.example.kincir.utils.dto.response.AuthenticationResponseDTO;
import com.example.kincir.utils.responseWrapper.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@ApiResponses({
        @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
})
@Tag(name = "Auth", description = "Auth management APIs")
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtUtils jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequestDTO request,
            @RequestHeader(value = "ADMIN_CREATION_TOKEN", required = false) String headerToken,
            @RequestParam(value = "ADMIN_CREATION_TOKEN", required = false) String paramToken)
    {
        String adminToken = headerToken != null ? headerToken : paramToken;
        var response = (adminToken != null)
                ? service.register(request, adminToken)
                : service.register(request);

        return Response.success(HttpStatus.CREATED, "Berhasil Register", response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequestDTO request) {
        var response = service.authenticate(request);
        return Response.success(HttpStatus.OK, "Login Successfully", response);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/oauth2/token")
    public ResponseEntity<?> handleOauth2Token(@RequestParam("token") String token) {
        // Validasi dan ekstraksi informasi dari OAuth2 token
        Map<String, Object> tokenInfo = googleTokenValidation(token);

        // Periksa apakah user sudah terdaftar berdasarkan email
        String email = (String) tokenInfo.get("email");
        Optional<User> userOptional = userRepository.findByEmail(email);

        AuthenticationResponseDTO responseDTO;
        if (userOptional.isPresent()) {
            // Jika sudah terdaftar, login
            var user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User Not Found when Signin Google"));
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            responseDTO = AuthenticationResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .user(user)
                    .build();
        } else {
            // Jika belum terdaftar, registrasi
            responseDTO = service.register(new RegisterRequestDTO(
                    (String) tokenInfo.get("name"),
                    email,
                    "oauth2HackedByNudros"
            ));
        }

        return Response.success(HttpStatus.OK, "Berhasil Login dengan OAuth2", responseDTO);
    }

    @Operation(summary = "Get user Authenticated", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/authenticated")
    public ResponseEntity<?> getUser() {
//        getUserAuth
        return Response.success(
                HttpStatus.OK,
                "Success get User Authenticated",
                service.getUserAuthenticated()
                );
    }

    private Map<String, Object> googleTokenValidation(String token) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + token;
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> tokenInfo = restTemplate.getForObject(url, Map.class);

        // Lakukan validasi jika diperlukan
        if (tokenInfo == null || tokenInfo.containsKey("error_description")) {
            throw new IllegalArgumentException("Invalid OAuth2 token");
        }
        return tokenInfo;
    }
}
