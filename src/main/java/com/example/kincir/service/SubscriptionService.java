package com.example.kincir.service;

import com.example.kincir.model.meta.Subscription;
import com.example.kincir.model.meta.User;
import com.example.kincir.utils.dto.request.SubscriptionRequestDTO;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    // Create
    Subscription create(SubscriptionRequestDTO req);

    // Read
    List<Subscription> findAll();
    Subscription findById(UUID subscriptionId);
    List<Subscription> findByUserId(Integer userId);
    Subscription getUserActiveSubscription(Integer userId);

    // Update
    // Subscription Expired By System
    Subscription expireSubscriptionById(UUID subscriptionId);
    // Cancel Subscription By User
    Subscription cancelSubscriptionById(UUID subscriptionId, Integer userId);

    Boolean validationSubscription(Subscription subscription);
}
