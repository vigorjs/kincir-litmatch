package com.example.kincir.seeder;

import com.example.kincir.service.AuthenticationService;
import com.example.kincir.utils.dto.request.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersSeederService {
    private final AuthenticationService authService;

    public void seederAdminAndUser() {
        List<RegisterRequestDTO> users = new ArrayList<>();
        users.add(new RegisterRequestDTO("admin1", "admin1@turing.com", "Batassuci123#"));
        users.add(new RegisterRequestDTO("admin2", "admin2@turing.com", "Batassuci123#"));
        users.add(new RegisterRequestDTO("string1", "string1@turing.com", "string"));
        users.add(new RegisterRequestDTO("string2", "string2@turing.com", "string"));

        for (var v : users) {
            if ("Batassuci123#".equals(v.getPassword())) {
                authService.register(v, "mdfker69");
            } else {
                authService.register(v);
            }
        }
        System.out.println("User seeder successfully executed");
    }
}
