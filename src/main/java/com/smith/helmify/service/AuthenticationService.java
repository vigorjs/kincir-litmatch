package com.smith.helmify.service;

import com.smith.helmify.model.meta.User;
import com.smith.helmify.utils.dto.AuthenticationRequestDTO;
import com.smith.helmify.utils.dto.AuthenticationResponseDTO;
import com.smith.helmify.utils.dto.RegisterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    public AuthenticationResponseDTO register(RegisterRequestDTO request);

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    public User getUserAuthenticated();
}
