package com.example.kincir.model.meta;

import com.example.kincir.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    @Min(message = "price can't be negative", value = 0)
    private Double price;

    @Column(name = "duration", nullable = true)
    @Min(message = "duration can't be negative", value = 0)
    private Integer duration;

    @Column(name = "isLifetime", nullable = false)
    private Boolean isLifetime;

    @Lob
    @Column(name = "description", nullable = true)
    private String description;

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
