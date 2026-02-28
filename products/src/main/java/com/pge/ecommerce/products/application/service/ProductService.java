package com.pge.ecommerce.products.application.service;

import com.pge.ecommerce.products.application.dto.ProductRequest;
import com.pge.ecommerce.products.application.dto.ProductResponse;
import com.pge.ecommerce.products.domain.model.Product;
import com.pge.ecommerce.products.domain.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse create(ProductRequest request) {
        if (productRepository.findByDescription(request.description()).isPresent()) {
            throw new IllegalArgumentException("Produto já existe: " + request.description());
        }

        Product product = Product.create(request.description(), request.category(), request.price());
        productRepository.save(product);
        return new ProductResponse(product.getId(), product.getDescription(), product.getCategory(), product.getPrice());
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(p -> new ProductResponse(p.getId(), p.getDescription(), p.getCategory(), p.getPrice())).toList();
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
        return new ProductResponse(product.getId(), product.getDescription(), product.getCategory(), product.getPrice());
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
        product.update(request.description(), request.category(), request.price());
        productRepository.save(product);
        return new ProductResponse(product.getId(), product.getDescription(), product.getCategory(), product.getPrice());
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
        productRepository.deleteById(product.getId());
    }
}
