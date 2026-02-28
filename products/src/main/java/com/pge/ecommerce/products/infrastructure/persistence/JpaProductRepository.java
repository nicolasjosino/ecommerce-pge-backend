package com.pge.ecommerce.products.infrastructure.persistence;

import com.pge.ecommerce.products.domain.model.Product;
import com.pge.ecommerce.products.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, Long>, ProductRepository {
    Optional<Product> findByDescription(String description);

    void deleteById(Long id);
}

