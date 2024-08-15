package com.virgo.ecommerce.repo.impl;

import com.virgo.ecommerce.model.enums.TransactionStatus;
import com.virgo.ecommerce.model.meta.Transaction;
import com.virgo.ecommerce.repo.TransactionRepository;
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
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id,  order_id, status, gross_amount) VALUES (?, ?, ?, ?) RETURNING id, user_id, order_id, status, gross_amount";
        return jdbcTemplate.queryForObject(sql, new Object[]{transaction.getUser_id(), transaction.getOrder_id(), transaction.getStatus(), transaction.getGross_amount()}, new TransactionRowMapper());
    }

    @Override
    public Optional<Transaction> findById(Integer id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try {
            Transaction transaction = jdbcTemplate.queryForObject(sql, new Object[]{id}, new TransactionRowMapper());
            return Optional.ofNullable(transaction);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> findByUserId(Integer userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, new TransactionRowMapper());
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions";
        return jdbcTemplate.query(sql, new TransactionRowMapper());
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET user_id = ?, order_id = ?, status = ?, gross_amount = ? WHERE id = ?";
        jdbcTemplate.update(sql, transaction.getUser_id(), transaction.getOrder_id(), transaction.getStatus(), transaction.getGross_amount(), transaction.getId());
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
            Transaction transaction = jdbcTemplate.queryForObject(sql, new TransactionRowMapper());
            return Optional.ofNullable(transaction);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static class TransactionRowMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Transaction.builder()
                    .id(rs.getInt("id"))
                    .user_id(rs.getInt("user_id"))
                    .order_id(rs.getString("order_id"))
                    .status(rs.getString("status"))
                    .gross_amount(rs.getLong("gross_amount"))
                    .build();
        }
    }
}