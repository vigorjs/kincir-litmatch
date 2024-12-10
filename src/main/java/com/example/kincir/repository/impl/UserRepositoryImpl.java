package com.example.kincir.repository.impl;

import com.example.kincir.model.BaseEntity;
import com.example.kincir.model.enums.UserRole;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        String sql = """
            INSERT INTO users (username, email, password, role, created_at, updated_at, photo) 
            VALUES (?, ?, ?, ?, ?, ?, ?) 
            RETURNING id, username, email, password, role, created_at, updated_at, photo
        """;
        return jdbcTemplate.queryForObject(sql,
                new Object[]{user.getRealUsername(), user.getEmail(), user.getPassword(), user.getRole().name(), LocalDateTime.now(), LocalDateTime.now(), user.getPhoto()},
                new UserRowMapper());
    }

    //Axel ganti bagian photo
    @Override
    public void update(User user) {
        String sql = """
            UPDATE users SET username = ?, email = ?, password = ?, role = ?, updated_at = ? , photo = ?
            WHERE id = ? 
            RETURNING id, username, email, password, role, created_at, updated_at, photo
        """;
        jdbcTemplate.queryForObject(sql,
                new Object[]{user.getRealUsername(), user.getEmail(), user.getPassword(), user.getRole().name(), LocalDateTime.now(), user.getPhoto(), user.getId()},
                new UserRowMapper());
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new Integer[]{id}, new UserRowMapper());
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new String[]{email}, new UserRowMapper());
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            BaseEntity baseEntity = new BaseEntity();
            baseEntity.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            baseEntity.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            return User.builder()
                    .id(rs.getInt("id"))
                    .username(rs.getString("username"))
                    .email(rs.getString("email"))
                    .password(rs.getString("password"))
                    .role(UserRole.valueOf(rs.getString("role")))
                    .photo(rs.getString("photo"))
                    .baseEntity(baseEntity)
                    .build();
        }
    }
}
