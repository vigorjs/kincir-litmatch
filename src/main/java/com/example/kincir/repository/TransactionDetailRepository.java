package com.example.kincir.repository;

import com.example.kincir.model.meta.TransactionDetail;

import java.util.List;
import java.util.Optional;

public interface TransactionDetailRepository {
    TransactionDetail save(TransactionDetail transactionDetail);
    Optional<TransactionDetail> findById(Integer id);
    List<TransactionDetail> findByTransactionId(Integer transactionId);
    List<TransactionDetail> findAll();
    void update(TransactionDetail transactionDetail);
    void deleteById(Integer id);
}