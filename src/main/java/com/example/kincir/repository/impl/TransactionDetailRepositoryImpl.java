package com.example.kincir.repository.impl;

import com.example.kincir.model.meta.Transaction;
import com.example.kincir.model.meta.TransactionDetail;
import com.example.kincir.repository.TransactionDetailRepository;
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
public class TransactionDetailRepositoryImpl implements TransactionDetailRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public TransactionDetail save(TransactionDetail transactionDetail) {
        String sql = "INSERT INTO transaction_details (transaction_id, amount, quantity) VALUES (?, ?, ?, ?, ?) RETURNING id, transaction_id, amount, quantity";
        return jdbcTemplate.queryForObject(sql, new Object[]{
                transactionDetail.getTransaction().getId(),
                transactionDetail.getAmount(),
                transactionDetail.getQuantity()
        }, new TransactionDetailRowMapper());
    }

    @Override
    public Optional<TransactionDetail> findById(Integer id) {
        String sql = "SELECT * FROM transaction_details WHERE id = ?";
        try {
            TransactionDetail transactionDetail = jdbcTemplate.queryForObject(sql, new Object[]{id}, new TransactionDetailRowMapper());
            return Optional.ofNullable(transactionDetail);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionDetail> findByTransactionId(Integer transactionId) {
        String sql = "SELECT * FROM transaction_details WHERE transaction_id = ?";
        return jdbcTemplate.query(sql, new Object[]{transactionId}, new TransactionDetailRowMapper());
    }

    @Override
    public List<TransactionDetail> findAll() {
        String sql = "SELECT * FROM transaction_details";
        return jdbcTemplate.query(sql, new TransactionDetailRowMapper());
    }

    @Override
    public void update(TransactionDetail transactionDetail) {
        String sql = "UPDATE transaction_details SET transaction_id = ?, amount = ?, quantity = ? WHERE id = ?";
        jdbcTemplate.update(sql, transactionDetail.getTransaction().getId(), transactionDetail.getAmount(), transactionDetail.getQuantity(), transactionDetail.getId());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM transaction_details WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class TransactionDetailRowMapper implements RowMapper<TransactionDetail> {

        @Override
        public TransactionDetail mapRow(ResultSet rs, int rowNum) throws SQLException {

            return TransactionDetail.builder()
                    .id(rs.getInt("id"))
                    .transaction(Transaction.builder().id(rs.getInt("transaction_id")).build())
                    .amount(rs.getLong("amount"))
                    .quantity(rs.getInt("quantity"))
                    .build();
        }
    }
}