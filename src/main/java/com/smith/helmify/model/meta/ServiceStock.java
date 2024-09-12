package com.smith.helmify.model.meta;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_stocks")
public class ServiceStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @NotNull
    @JoinColumn(name = "serviceId", nullable = false)
    private Service service;

    @Column(name = "quantity", nullable = true)
    @Min(message = "serviceStock quantity cant be negative", value = 0)
    private Integer quantity;
}