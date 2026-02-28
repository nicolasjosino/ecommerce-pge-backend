package com.pge.ecommerce.products.application.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String description, String category, BigDecimal price) {
}
