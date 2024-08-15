package com.virgo.ecommerce.service.impl;

import com.virgo.ecommerce.config.advisers.exception.NotFoundException;
import com.virgo.ecommerce.model.meta.Store;
import com.virgo.ecommerce.model.meta.User;
import com.virgo.ecommerce.repo.StoreRepository;
import com.virgo.ecommerce.service.AuthenticationService;
import com.virgo.ecommerce.service.StoreService;
import com.virgo.ecommerce.utils.dto.StoreRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final AuthenticationService authenticationService;

    @Override
    public Store create(StoreRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();
        Store store = Store.builder()
                .user_id(user.getId())
                .store_name(req.getStore_name())
                .store_description(req.getStore_description())
                .build();
        return storeRepository.save(store);
    }

    @Override
    public List<Store> getAll() {
        return storeRepository.findAll();
    }

    @Override
    public Store getById(Integer id) {
        return storeRepository.findById(id).orElseThrow( () -> new NotFoundException("Store not Found") );
    }

    @Override
    public void delete(Integer id) {
        if (storeRepository.findById(id).isPresent()){
            storeRepository.deleteById(id);
        }else {
            throw new NotFoundException("Category dengan ID " + id + "tidak ditemukan");
        }
    }

    @Override
    public Store updateById(Integer id, StoreRequestDTO req) {
        Store target = getById(id);
        Store storeRequest = updateStoreRequest(target, req);
        storeRepository.update(storeRequest);

        return target;
    }

    @Override
    public Store update(StoreRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();
        Store currentStore = storeRepository.findByUserId(user.getId()).orElseThrow();
        Store storeRequest = updateStoreRequest(currentStore, req);
        storeRepository.update(storeRequest);

        return currentStore;
    }

    //helper
    private Store updateStoreRequest(Store store, StoreRequestDTO req) {
        if (req.getStore_name() != null && !req.getStore_name().isEmpty()) {
            store.setStore_name(req.getStore_name());
        }
        if (req.getStore_description() != null && !req.getStore_description().isEmpty()) {
            store.setStore_description(req.getStore_description());
        }
        return new Store(
                store.getId(),
                store.getUser_id(),
                req.getStore_name() != null ? req.getStore_name() : store.getStore_name(),
                req.getStore_description() != null ? req.getStore_description() : store.getStore_description()
        );
    }
}
