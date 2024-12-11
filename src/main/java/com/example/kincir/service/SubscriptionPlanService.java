package com.example.kincir.service;

import com.example.kincir.model.meta.SubscriptionPlan;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanService {
    //Create
    SubscriptionPlan create(SubscriptionPlan req);
    //Read
    SubscriptionPlan findById(UUID subscriptionPlanId);
    List<SubscriptionPlan> findAll();
    //Update
    SubscriptionPlan updateById(UUID subscriptionPlanId, SubscriptionPlan reqSubscriptionPlanUpdate);
    //Delete
    void deleteById(UUID subscriptionPlanId);
}
