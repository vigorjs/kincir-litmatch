package com.example.kincir.service.impl;

import com.example.kincir.config.security.advisers.exception.NotFoundException;
import com.example.kincir.model.enums.SubscriptionStatus;
import com.example.kincir.model.meta.Subscription;
import com.example.kincir.model.meta.SubscriptionPlan;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.SubscriptionRepository;
import com.example.kincir.service.AuthenticationService;
import com.example.kincir.service.SubscriptionService;
import com.example.kincir.service.UserService;
import com.example.kincir.utils.dto.request.SubscriptionRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    //Required Repository
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    @Override
    public Subscription create(SubscriptionRequestDTO req) {
        Long now = new Date().getTime();
        //Input Subscription request data
        Subscription newSubscription = Subscription.builder()
                .user(req.getUser())
                .plan(req.getSubscriptionPlan())
                .startDate(now)
                .endDate(now + req.getSubscriptionPlan().getDuration())
                .status(SubscriptionStatus.ACTIVE)
                .build();

        //Return Subscription
        return subscriptionRepository.save(newSubscription);
    }

    @Override
    public List<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription findById(UUID subscriptionId) {
        return subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new NotFoundException("Subscription Not Found"));
    }

    @Override
    public List<Subscription> findByUserId(Integer userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public Subscription getUserActiveSubscription(Integer userId) {
        List<Subscription> subscriptions = findByUserId(userId);

        // Filter status ACTIVE dan ambil subscription dengan endDate terbaru
        return subscriptions.stream()
                .filter(subscription -> subscription.getStatus() == SubscriptionStatus.ACTIVE)
                .max(Comparator.comparing(Subscription::getEndDate))
                .orElseThrow(() -> new NotFoundException("No active subscription found for user with ID: " + userId));
    }



    @Override
    public Subscription expireSubscriptionById(UUID subscriptionId) {
        // Get Subscription
        Subscription foundSubscription = findById(subscriptionId);

        // Validate End Date
        Boolean isExpired = validateSubscriptionTime(foundSubscription);

        if (isExpired) {
            foundSubscription.setStatus(SubscriptionStatus.EXPIRED);
        }

        return findById(foundSubscription.getId());
    }

    @Override
    public Subscription cancelSubscriptionById(UUID subscriptionId, Integer userId) {
        // Get Current User
        User currentUser = userService.getById(userId);

        // Get Subscription
        Subscription foundSubscription = findById(subscriptionId);

        // Validate Authorization User
        validateSubscriptionUser(currentUser, foundSubscription);

        // Edit Status Subscription
        foundSubscription.setStatus(SubscriptionStatus.CANCELLED);

        // Return Update Subscription
        return findById(foundSubscription.getId());
    }

    @Override
    public Boolean validationSubscription(Integer userId) {

        Subscription subscription = getUserActiveSubscription(userId);
        SubscriptionPlan plan = subscription.getPlan();

        if (plan.getIsLifetime()) return true;

        long now = new Date().getTime();
        if (now > subscription.getEndDate()){
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            return false;
        }

        return true;
    }

    private void validateSubscriptionUser(User user, Subscription subscription){
        if (subscription.getUser() != user){
            throw new AuthorizationDeniedException("Access Denied", new AuthorizationResult() {
                @Override
                public boolean isGranted() {
                    return false; // Denied
                }

                @Override
                public String toString() {
                    return "This subscription doesn't belong to you";
                }
            });
        }
    }

    private Boolean validateSubscriptionTime(Subscription subscription){
        // Get Current Time
        Long currentTime = new Date().getTime();

        // Get Current Subscription Time
        Long subscriptionEndTime = subscription.getEndDate();

        // Validate
        if (subscriptionEndTime > currentTime){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
