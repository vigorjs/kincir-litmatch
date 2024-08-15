package com.virgo.ecommerce.service;

import com.virgo.ecommerce.model.meta.User;
import com.virgo.ecommerce.utils.dto.AuthenticationRequestDTO;
import com.virgo.ecommerce.utils.dto.AuthenticationResponseDTO;
import com.virgo.ecommerce.utils.dto.RegisterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    public AuthenticationResponseDTO register(RegisterRequestDTO request);

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    public User getUserAuthenticated();
}
