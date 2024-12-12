package com.example.kincir.seeder;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RunnerSeeder {

    @Value("${seed.database}")
    private Boolean seedDatabase;

    private final SubscriptionPlanSeederService subscriptionPlanSeederService;
    private final UsersSeederService usersSeederService;


    @PostConstruct
    public void runSeeders() {
        if (!seedDatabase) {
            return;
        }
        subscriptionPlanSeederService.seederSubscriptionPlan();
        usersSeederService.seederAdminAndUser();
    }
}
