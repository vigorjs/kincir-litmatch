package com.virgo.ecommerce.service;

import com.virgo.ecommerce.model.meta.Store;
import com.virgo.ecommerce.utils.dto.StoreRequestDTO;

import java.util.List;

public interface StoreService {
    Store create(StoreRequestDTO req);
    List<Store> getAll();
    Store getById(Integer id);
    void delete(Integer id);
    Store updateById(Integer id, StoreRequestDTO req);
    //    Store update by user authententicated
    Store update(StoreRequestDTO req);
}
