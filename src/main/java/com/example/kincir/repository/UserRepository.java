package com.example.kincir.repository;

import com.example.kincir.model.meta.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Integer id);
    List<User> findAll();
    Optional<User> findByEmail(String email);
    void update(User user);
    void deleteById(Integer id);
}
