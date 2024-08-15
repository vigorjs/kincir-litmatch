package com.virgo.ecommerce.repo.impl;

import com.virgo.ecommerce.model.meta.Store;
import com.virgo.ecommerce.model.meta.User;
import com.virgo.ecommerce.repo.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Store save(Store store) {
        String sql = "INSERT INTO stores (store_name, store_description, user_id) VALUES (?, ?, ?) RETURNING id, store_name, store_description, user_id";
        return jdbcTemplate.queryForObject(sql, new Object[]{store.getStore_name(), store.getStore_description(), store.getUser_id()}, new StoreRepositoryImpl.StoreRowMapper());
    }

    @Override
    public void update(Store store) {
        String sql = "UPDATE stores SET store_name = ?, store_description = ?, user_id = ? WHERE id = ? RETURNING id, store_name, store_description, user_id";
        jdbcTemplate.queryForObject(sql, new Object[]{store.getStore_name(), store.getStore_description(), store.getUser_id(), store.getId()}, new StoreRepositoryImpl.StoreRowMapper());
    }

    @Override
    public Optional<Store> findById(Integer id) {
        String sql = "SELECT * FROM stores WHERE id = ?";
        try {
            Store store = jdbcTemplate.queryForObject(sql, new Integer[]{id}, new StoreRepositoryImpl.StoreRowMapper());
            return Optional.of(store);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Store> findByUserId(Integer userId) {
        String sql = "SELECT * FROM stores WHERE user_id = ?";
        try {
            Store store = jdbcTemplate.queryForObject(sql, new Integer[]{userId}, new StoreRepositoryImpl.StoreRowMapper());
            return Optional.of(store);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM stores WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Store> findAll() {
        String sql = "SELECT * FROM stores";
        return jdbcTemplate.query(sql, new StoreRepositoryImpl.StoreRowMapper());
    }

    private static class StoreRowMapper implements RowMapper<Store> {

        @Override
        public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Store.builder()
                    .id(rs.getInt("id"))
                    .user_id(rs.getInt("user_id"))
                    .store_name(rs.getString("store_name"))
                    .store_description(rs.getString("store_description"))
                    .build();
        }
    }
}
