package com.pge.ecommerce.sales.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SaleRequest(
        @NotNull(message = "productId é obrigatório")
        Long productId,

        @NotNull(message = "quantity é obrigatória")
        @Min(value = 1, message = "quantity deve ser pelo menos 1")
        Integer quantity
) {}

