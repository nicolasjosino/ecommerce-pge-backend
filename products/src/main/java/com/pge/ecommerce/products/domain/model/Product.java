package com.pge.ecommerce.products.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    public static Product create(String description, String category, BigDecimal price) {
        Product p = new Product();
        p.description = description;
        p.category = category;
        p.price = price;
        return p;
    }

    public void update(String description, String category, BigDecimal price) {
        this.description = description;
        this.category = category;
        this.price = price;
    }


}
