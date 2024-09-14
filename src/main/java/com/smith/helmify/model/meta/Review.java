package com.smith.helmify.model.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smith.helmify.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "stars", nullable = false)
    @Min(message = "stars cant be negative", value = 0)
    @Max(message = "stars cant be more than 5", value = 5)
    private Integer stars;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = true, length = 500)
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
        if (baseEntity == null) {
            baseEntity = new BaseEntity();
        }
        baseEntity.setUpdatedAt(LocalDateTime.now());
    }
}