package com.example.kincir.seeder;

import com.example.kincir.repository.SubscriptionPlanRepository;
import com.example.kincir.service.AuthenticationService;
import com.example.kincir.service.SubscriptionPlanService;
import com.example.kincir.model.meta.SubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanSeederService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public void seederSubscriptionPlan() {
        List<SubscriptionPlan> plans = new ArrayList<>();
        plans.add(SubscriptionPlan.builder().name("FREE TRIAL").price((double) 0).description("Free Trial 1 Week").isLifetime(false).duration(604800).build());
        plans.add(SubscriptionPlan.builder().name("MONTHLY").price((double) 50000).description("Monthly Subscription for 1 Month").isLifetime(false).duration(604800 * 4).build());
        plans.add(SubscriptionPlan.builder().name("LIFETIME").price((double) 0).description("Lifetime Subscription").isLifetime(true).duration(999999999).build());

        subscriptionPlanRepository.saveAll(plans);
        System.out.println("SubscriptionPlan seeder successfully executed");
    }
}
