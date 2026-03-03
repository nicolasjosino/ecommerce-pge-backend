package com.pge.ecommerce.sales.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String userEmail;           // email extraido do JWT


    @Column(nullable = false)
    private Long productId;


    @Column(nullable = false)
    private Integer quantity;


    @Column(nullable = false)
    private BigDecimal unitPrice;


    @Column(nullable = false)
    private BigDecimal totalPrice;


    @Column(nullable = false)
    private LocalDateTime saleDate;

    public static Sale create(String userEmail, Long productId,
                              String productDescription, Integer quantity,
                              BigDecimal unitPrice) {
        Sale sale = new Sale();
        sale.userEmail = userEmail;
        sale.productId = productId;
        sale.quantity = quantity;
        sale.unitPrice = unitPrice;
        sale.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        sale.saleDate = LocalDateTime.now();
        return sale;
    }


}
