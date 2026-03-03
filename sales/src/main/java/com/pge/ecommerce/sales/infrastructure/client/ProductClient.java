package com.pge.ecommerce.sales.infrastructure.client;

import com.pge.ecommerce.sales.application.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "products") // nome registrado no Eureka
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse findById(@PathVariable("id") Long id);
}

