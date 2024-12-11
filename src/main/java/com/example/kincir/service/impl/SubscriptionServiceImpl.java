package com.example.kincir.service.impl;

import com.example.kincir.config.security.advisers.exception.NotFoundException;
import com.example.kincir.model.enums.SubscriptionStatus;
import com.example.kincir.model.meta.Subscription;
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
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    //Required Repository
    private final SubscriptionRepository subscriptionRepository;

    //Required Service
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Override
    public Subscription create(SubscriptionRequestDTO req) {
        //Get Authenticated user
        User currentUser = authenticationService.getUserAuthenticated();

        //Input Subscription request data
        Subscription newSubscription = Subscription.builder()
                .user(currentUser)
                .plan(req.getSubscriptionPlan())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(req.getSubscriptionPlan().getDuration()))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        //Return Subscription
        return findById(newSubscription.getId());
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
        User user = userService.getById(userId);
        return subscriptionRepository.findByUser(user);
    }

    @Override
    public List<Subscription> findByCurrentUser() {
        User currentUser = authenticationService.getUserAuthenticated();
        return subscriptionRepository.findByUser(currentUser);
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
    public Subscription cancelSubscriptionById(UUID subscriptionId) {
        // Get Current User
        User currentUser = authenticationService.getUserAuthenticated();

        // Get Subscription
        Subscription foundSubscription = findById(subscriptionId);

        // Validate Authorization User
        validateSubscriptionUser(currentUser, foundSubscription);

        // Edit Status Subscription
        foundSubscription.setStatus(SubscriptionStatus.CANCELLED);

        // Return Update Subscription
        return findById(foundSubscription.getId());
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
        LocalDateTime currentTime = LocalDateTime.now();

        // Get Current Subscription Time
        LocalDateTime subscriptionEndTime = subscription.getEndDate();

        // Validate
        if (subscriptionEndTime.isAfter(currentTime)){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
