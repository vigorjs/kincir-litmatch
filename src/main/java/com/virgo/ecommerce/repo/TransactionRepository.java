package com.virgo.ecommerce.repo;

import com.virgo.ecommerce.model.meta.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Integer id);
    List<Transaction> findByUserId(Integer userId);
    List<Transaction> findAll();
    void update(Transaction transaction);
    void deleteById(Integer id);
    Optional<Transaction> findTopByOrderByIdDesc();
}