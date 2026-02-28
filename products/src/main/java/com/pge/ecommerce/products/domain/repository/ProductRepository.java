package com.pge.ecommerce.products.domain.repository;

import com.pge.ecommerce.products.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findByDescription(String description);

    List<Product> findAll();

    void deleteById(Long id);

}
