package com.virgo.ecommerce.service.impl;

import com.virgo.ecommerce.service.AuthenticationService;
import com.virgo.ecommerce.service.UserService;
import com.virgo.ecommerce.config.advisers.exception.NotFoundException;
import com.virgo.ecommerce.model.meta.User;
import com.virgo.ecommerce.repo.UserRepository;
import com.virgo.ecommerce.utils.dto.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    @Override
    public User create(RegisterRequestDTO req) {
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Integer id) {
        return userRepository.findById(id).orElseThrow( () -> new NotFoundException("User not Found") );
    }

    @Override
    public void delete(Integer id) {
        if (userRepository.findById(id).isPresent()){
            userRepository.deleteById(id);
        }else {
            throw new NotFoundException("Category dengan ID " + id + "tidak ditemukan");
        }
    }

    @Override
    public User updateById(Integer id, RegisterRequestDTO req) {
        User target = getById(id);
        User user = updateUserDetails(target, req);
        userRepository.update(user);

        return target;
    }

    @Override
    public User update(RegisterRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();
        updateUserDetails(user, req);
        userRepository.update(user);

        return user;
    }

    private User updateUserDetails(User user, RegisterRequestDTO req) {
        if (req.getUsername() != null && !req.getUsername().isEmpty()) {
            user.setUsername(req.getUsername());
        }
        if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            user.setEmail(req.getEmail());
        }
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        return new User(
                user.getId(),
                req.getUsername() != null ? req.getUsername() : user.getRealUsername(),
                req.getEmail() != null ? req.getEmail() : user.getEmail(),
                req.getPassword() != null ? passwordEncoder.encode(req.getPassword()) : user.getPassword()
        );
    }
}
