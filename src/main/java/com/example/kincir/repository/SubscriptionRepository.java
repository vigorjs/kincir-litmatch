package com.example.kincir.repository;

import com.example.kincir.model.meta.Subscription;
import com.example.kincir.model.meta.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByUser(User user);
}
