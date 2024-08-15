package com.virgo.ecommerce.service;

import com.virgo.ecommerce.model.meta.Product;
import com.virgo.ecommerce.utils.dto.ProductRequestDTO;
import com.virgo.ecommerce.utils.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO create(ProductRequestDTO req);
    List<ProductDTO> getAll();
    ProductDTO getById(Integer id);
    List<ProductDTO> getByStoreId(Integer storeId);
    void delete(Integer id);
    ProductDTO updateById(Integer id, ProductRequestDTO req);
}