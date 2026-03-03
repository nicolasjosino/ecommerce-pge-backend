package com.pge.ecommerce.sales.application.dto;

import com.pge.ecommerce.sales.domain.model.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleResponse(
        Long id,
        String userEmail,
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        LocalDateTime saleDate
) {

    public static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getId(), sale.getUserEmail(), sale.getProductId(), sale.getQuantity(),
                sale.getUnitPrice(), sale.getTotalPrice(), sale.getSaleDate()
        );
    }

}
