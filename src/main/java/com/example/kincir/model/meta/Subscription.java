package com.example.kincir.model.meta;

import com.example.kincir.model.BaseEntity;
import com.example.kincir.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "subscriptionPlanId", referencedColumnName = "id", nullable = false)
    private SubscriptionPlan plan;

    @Column(name = "startDate", nullable = false)
    private Long startDate;

    @Column(name = "endDate", nullable = false)
    private Long endDate;

    @Column(name = "subscriptionStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Embedded
    private BaseEntity baseEntity = new BaseEntity();

    @PrePersist
    protected void onCreate() {
        if (baseEntity == null) {
            baseEntity = new BaseEntity();
        }
        baseEntity.setCreatedAt(LocalDateTime.now());
        baseEntity.setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        if (baseEntity == null){
            baseEntity = new BaseEntity();
        }
        baseEntity.setUpdatedAt(LocalDateTime.now());
    }

}
