package com.example.kincir.model.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_details")
public class TransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "machine_id", referencedColumnName = "id", nullable = false)
//    @JsonIgnore
//    private Machine machine;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
//    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Transaction transaction;

    @Column(name = "amount", nullable = true)
    private Long amount;

    @Column(name = "quantity", nullable = true)
    private Integer quantity;

}