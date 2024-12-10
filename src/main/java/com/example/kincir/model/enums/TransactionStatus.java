package com.example.kincir.model.enums;

public enum TransactionStatus {
    authorize,
    capture,
    settlement,
    deny,
    pending,
    cancel,
    refund,
    partial_refund,
    chargeback,
    partial_chargeback,
    expire,
    failure
}
