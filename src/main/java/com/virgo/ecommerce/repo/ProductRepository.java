package com.virgo.ecommerce.repo;

import com.virgo.ecommerce.model.meta.Product;
import com.virgo.ecommerce.utils.dto.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    ProductDTO save(Product product);
    Optional<ProductDTO> findById(Integer id);
    List<ProductDTO> findAll();
    List<ProductDTO> findByStoreId(Integer storeId);
    void update(Product product);
    void deleteById(Integer id);
}