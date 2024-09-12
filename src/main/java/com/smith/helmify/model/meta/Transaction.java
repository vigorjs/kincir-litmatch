package com.smith.helmify.model.meta;

import com.smith.helmify.model.BaseEntity;
import com.smith.helmify.model.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "order_id", nullable = true)
    private String order_id;


    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;


    @Column(name = "gross_amount")
    @Min(message = "price cant be negative", value = 0)
    private Long gross_amount;

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