package com.virgo.ecommerce.repo;

import com.virgo.ecommerce.model.meta.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepository {
    Store save(Store store);
    Optional<Store> findById(Integer id);
    Optional<Store> findByUserId(Integer userId);
    List<Store> findAll();
    void update(Store user);
    void deleteById(Integer id);
}
