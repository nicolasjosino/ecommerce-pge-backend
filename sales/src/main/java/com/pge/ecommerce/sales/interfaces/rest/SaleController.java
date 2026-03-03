package com.pge.ecommerce.sales.interfaces.rest;

import com.pge.ecommerce.sales.application.dto.SaleRequest;
import com.pge.ecommerce.sales.application.dto.SaleResponse;
import com.pge.ecommerce.sales.application.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {


    private final SaleService saleService;


    @PostMapping
    public ResponseEntity<SaleResponse> create(
            @Valid @RequestBody SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saleService.createSale(request));
    }


    @GetMapping
    public ResponseEntity<List<SaleResponse>> findSales() {
        return ResponseEntity.ok(saleService.findMySales());
    }
}

