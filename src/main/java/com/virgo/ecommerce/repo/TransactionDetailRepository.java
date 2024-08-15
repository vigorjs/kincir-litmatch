package com.virgo.ecommerce.repo;

import com.virgo.ecommerce.model.meta.TransactionDetail;

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