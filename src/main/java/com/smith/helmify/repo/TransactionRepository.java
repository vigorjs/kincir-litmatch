package com.smith.helmify.repo;

import com.smith.helmify.model.meta.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Integer id);
    List<Transaction> findByUserId(Integer userId);
    List<Transaction> findAll(Integer userId);
    void update(Transaction transaction);
    void deleteById(Integer id);
    Optional<Transaction> findTopByOrderByIdDesc();
    Optional<Transaction> findByOrderId(String order_id);
}