package com.smith.helmify.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceStockRepository extends JpaRepository<com.smith.helmify.model.meta.ServiceStock, Integer> {
    Optional<com.smith.helmify.model.meta.ServiceStock> findByServiceId(Integer serviceId);
}
