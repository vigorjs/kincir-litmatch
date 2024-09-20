package com.smith.helmify.controller;

import com.smith.helmify.config.JwtService;
import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.meta.User;
import com.smith.helmify.repo.UserRepository;
import com.smith.helmify.service.AuthenticationService;
import com.smith.helmify.utils.dto.AuthenticationRequestDTO;
import com.smith.helmify.utils.dto.AuthenticationResponseDTO;
import com.smith.helmify.utils.dto.RegisterRequestDTO;
import com.smith.helmify.utils.responseWrapper.WebResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RestControllerAdvice
@RequiredArgsConstructor
@ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "403", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })
})
@Tag(name = "Auth", description = "Auth management APIs")
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequestDTO request) {
        return new ResponseEntity(new WebResponse("Berhasil Register", HttpStatus.OK, service.register(request)), HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@Valid @RequestBody AuthenticationRequestDTO request) {
        return new ResponseEntity(new WebResponse("Login Successfully", HttpStatus.OK, service.authenticate(request)), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/oauth2/token")
    public ResponseEntity<AuthenticationResponseDTO> handleOauth2Token(@RequestParam("token") String token) {
        // Validasi dan ekstraksi informasi dari OAuth2 token (misalnya Google)
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

        return new ResponseEntity(new WebResponse("Berhasil Login dengan OAuth2", HttpStatus.OK, responseDTO), HttpStatus.OK);
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
