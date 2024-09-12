package com.smith.helmify.repo.impl;

import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.BaseEntity;
import com.smith.helmify.model.enums.TransactionStatus;
import com.smith.helmify.model.meta.Transaction;
import com.smith.helmify.repo.TransactionRepository;
import com.smith.helmify.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Override
    public Transaction save(Transaction transaction) {
        String sql = """
        INSERT INTO transactions (user_id, order_id, status, gross_amount, created_at, updated_at) 
        VALUES (?, ?, ?, ?, ?, ?) 
        RETURNING id, user_id, order_id, status, gross_amount, created_at, updated_at
    """;
        return jdbcTemplate.queryForObject(sql,
                new Object[]{transaction.getUser().getId(), transaction.getOrder_id(), transaction.getStatus().name(), transaction.getGross_amount(), LocalDateTime.now(), LocalDateTime.now()},
                new TransactionRowMapper(userRepository));
    }


    @Override
    public Optional<Transaction> findById(Integer id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try {
            Transaction transaction = jdbcTemplate.queryForObject(sql, new Object[]{id}, new TransactionRowMapper(userRepository));
            return Optional.ofNullable(transaction);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Transaction> findByOrderId(String order_id) {
        String sql = "SELECT * FROM transactions WHERE order_id = ?";
        try {
            Transaction transaction = jdbcTemplate.queryForObject(sql, new Object[]{order_id}, new TransactionRowMapper(userRepository));
            return Optional.ofNullable(transaction);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> findByUserId(Integer userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new TransactionRowMapper(userRepository));
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions";
        return jdbcTemplate.query(sql, new TransactionRowMapper(userRepository));
    }

    @Override
    public void update(Transaction transaction) {
        String sql = """
        UPDATE transactions SET user_id = ?, order_id = ?, status = ?, gross_amount = ?, updated_at = ? 
        WHERE id = ? 
        RETURNING id, user_id, order_id, status, gross_amount, created_at, updated_at
    """;
        jdbcTemplate.queryForObject(sql,
                new Object[]{transaction.getUser().getId(), transaction.getOrder_id(), transaction.getStatus().name(), transaction.getGross_amount(), LocalDateTime.now(), transaction.getId()},
                new TransactionRowMapper(userRepository));
    }


    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Transaction> findTopByOrderByIdDesc() {
        String sql = "SELECT * FROM transactions ORDER BY id DESC LIMIT 1";
        try {
            Transaction transaction = jdbcTemplate.queryForObject(sql, new TransactionRowMapper(userRepository));
            return Optional.ofNullable(transaction);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class TransactionRowMapper implements RowMapper<Transaction> {
        private UserRepository userRepository;

        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            BaseEntity baseEntity = new BaseEntity();
            baseEntity.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            baseEntity.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
            return Transaction.builder()
                    .id(rs.getInt("id"))
                    .user(userRepository.findById(rs.getInt("user_id")).orElseThrow(() -> new NotFoundException("User Not Found")))
                    .order_id(rs.getString("order_id"))
                    .status(TransactionStatus.valueOf(rs.getString("status")))
                    .gross_amount(rs.getLong("gross_amount"))
                    .baseEntity(baseEntity)
                    .build();
        }
    }

}
