package com.example.kincir.service.impl;

import com.example.kincir.config.security.advisers.exception.NotFoundException;
import com.example.kincir.model.enums.UserRole;
import com.example.kincir.model.meta.SubscriptionPlan;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.SubscriptionPlanRepository;
import com.example.kincir.service.AuthenticationService;
import com.example.kincir.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {
    //Required Repository
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    //Required Service
    private final AuthenticationService authenticationService;

    private void validateUserRoleAdmin(){
        User user = authenticationService.getUserAuthenticated();
        if (user.getRole() != UserRole.ADMIN) {
            throw new AuthorizationDeniedException("Access Denied", new AuthorizationResult() {
                @Override
                public boolean isGranted() {
                    return false; // Denied
                }

                @Override
                public String toString() {
                    return "User does not have the ADMIN role.";
                }
            });
        }
    }

    @Override
    public SubscriptionPlan create(SubscriptionPlan req) {
        //Validated User (Create Subscription must Admin)
        validateUserRoleAdmin();

        //Create Builder
        SubscriptionPlan newSubscriptionPlan = SubscriptionPlan.builder()
                .name(req.getName())
                .price(req.getPrice())
                .duration(req.getIsLifetime() ? req.getDuration() : null)
                .isLifetime(req.getIsLifetime())
                .description(req.getDescription())
                .build();

        subscriptionPlanRepository.save(newSubscriptionPlan);

        //Return Object Subscription Plan
        return findById(newSubscriptionPlan.getId());
    }

    @Override
    public SubscriptionPlan findById(UUID subscriptionPlanId) {
        //Return Object of Subscription Plan
        return subscriptionPlanRepository.findById(subscriptionPlanId).orElseThrow(() -> new NotFoundException("Subscription Doesn't Exist"));
    }

    @Override
    public List<SubscriptionPlan> findAll() {
        //Return List of Object of Subscription Plan
        return subscriptionPlanRepository.findAll();
    }

    @Override
    public SubscriptionPlan updateById(UUID subscriptionPlanId, SubscriptionPlan reqSubscriptionPlanUpdate) {
        //Validate User Role
        validateUserRoleAdmin();

        //Validate Input
        if (reqSubscriptionPlanUpdate == null){
            throw new IllegalArgumentException("Subscription Plan Update Request Cannot be Null");
        }

        //Get Exact Subscription Plan
        SubscriptionPlan foundSubscriptionPlan = findById(subscriptionPlanId);

        //Update Subscription
        foundSubscriptionPlan.setName(reqSubscriptionPlanUpdate.getName());
        foundSubscriptionPlan.setPrice(reqSubscriptionPlanUpdate.getPrice());
        foundSubscriptionPlan.setDuration(reqSubscriptionPlanUpdate.getIsLifetime() ? null : reqSubscriptionPlanUpdate.getDuration());
        foundSubscriptionPlan.setIsLifetime(reqSubscriptionPlanUpdate.getIsLifetime());
        foundSubscriptionPlan.setDescription(reqSubscriptionPlanUpdate.getDescription());

        subscriptionPlanRepository.save(foundSubscriptionPlan);

        //Return Object of Updated Subscription Plan
        return findById(foundSubscriptionPlan.getId());
    }

    @Override
    public void deleteById(UUID subscriptionPlanId) {
        //Validated User
        validateUserRoleAdmin();

        //Validate Subscription plan is exist
        if (!subscriptionPlanRepository.existsById(subscriptionPlanId)){
            throw new NotFoundException("Subscription Not Found");
        }

        //Delete Subscription Plan
        subscriptionPlanRepository.deleteById(subscriptionPlanId);
    }
}
