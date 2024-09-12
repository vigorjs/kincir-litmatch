package com.smith.helmify.model.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smith.helmify.model.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "price", nullable = false)
    @Min(message = "price cant be negative", value = 0)
    private Long price;

    @Column(name = "serviceName", nullable = false, length = 100, unique = true)
    private String service_name;

    @Column(name = "serviceDescription", nullable = true, length = 500)
    private String service_description;

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