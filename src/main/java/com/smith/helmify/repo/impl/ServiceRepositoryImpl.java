package com.smith.helmify.repo.impl;

import com.smith.helmify.model.meta.Service;
import com.smith.helmify.repo.ServiceRepository;
import com.smith.helmify.utils.dto.ServiceDTO;
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
public class ServiceRepositoryImpl implements ServiceRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public ServiceDTO save(Service service) {
        String sql = """
        INSERT INTO services (user_id, service_name, service_description, price, created_at, updated_at) 
        VALUES (?, ?, ?, ?, ?, ?) 
        RETURNING id, user_id, service_name, service_description, price, created_at, updated_at
    """;
        return jdbcTemplate.queryForObject(sql,
                new Object[]{service.getUser().getId(), service.getService_name(), service.getService_description(), service.getPrice(), LocalDateTime.now(), LocalDateTime.now()},
                new ServiceRowMapper());
    }


    @Override
    public Optional<ServiceDTO> findById(Integer id) {
        String sql = """
            SELECT services.*, ss.quantity AS stock
            FROM services 
            JOIN service_stocks ss ON services.id = ss.service_id
            WHERE services.id = ?
        """;
        try {
            ServiceDTO service = jdbcTemplate.queryForObject(sql, new Object[]{id}, new ServiceRowMapper());
            return Optional.ofNullable(service);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ServiceDTO> findAll() {
        String sql = """
            SELECT services.*, ss.quantity AS stock
            FROM services 
            JOIN service_stocks ss ON services.id = ss.service_id
        """;
        return jdbcTemplate.query(sql, new ServiceRowMapper());
    }

    @Override
    public List<ServiceDTO> findByUserId(Integer userId) {
        String sql = """
            SELECT services.*, ss.quantity AS stock
            FROM services 
            JOIN service_stocks ss ON services.id = ss.service_id
            WHERE services.user_id = ?
        """;
        return jdbcTemplate.query(sql, new Object[]{userId}, new ServiceRowMapper());
    }

    @Override
    public void update(Service service) {
        String sql = """
        UPDATE services SET user_id = ?, service_name = ?, service_description = ?, price = ?, updated_at = ? 
        WHERE id = ? 
        RETURNING id, user_id, service_name, service_description, price, created_at, updated_at
    """;
        jdbcTemplate.queryForObject(sql,
                new Object[]{service.getUser().getId(), service.getService_name(), service.getService_description(), service.getPrice(), LocalDateTime.now(), service.getId()},
                new ServiceRowMapper());
    }


    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM services WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class ServiceRowMapper implements RowMapper<ServiceDTO> {
        @Override
        public ServiceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            Integer stock = null;
            try {
                stock = rs.getInt("stock");
            } catch (SQLException e) {
                System.out.println("sql error when getting stock: " + e.getMessage());
            }
            return ServiceDTO.builder()
                    .id(rs.getInt("id"))
                    .service_name(rs.getString("service_name"))
                    .service_description(rs.getString("service_description"))
                    .price(rs.getLong("price"))
                    .stock(stock)
                    .build();
        }
    }
}