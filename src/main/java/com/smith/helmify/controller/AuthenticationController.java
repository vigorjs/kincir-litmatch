package com.smith.helmify.controller;

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

import java.io.IOException;

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

}
