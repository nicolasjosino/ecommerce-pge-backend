package com.pge.ecommerce.sales.application.service;

import com.pge.ecommerce.sales.application.dto.ProductResponse;
import com.pge.ecommerce.sales.application.dto.SaleRequest;
import com.pge.ecommerce.sales.application.dto.SaleResponse;
import com.pge.ecommerce.sales.domain.model.Sale;
import com.pge.ecommerce.sales.domain.repository.SaleRepository;
import com.pge.ecommerce.sales.infrastructure.client.ProductClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductClient productClient;

    public SaleResponse createSale(SaleRequest request) {
        String userEmail = (String) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        ProductResponse product;
        try {
            product = productClient.findById(request.productId());
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(
                    "Produto não encontrado: " + request.productId());
        } catch (FeignException e) {
            throw new IllegalStateException(
                    "Erro ao consultar Products Service: " + e.getMessage());
        }

        Sale sale = Sale.create(
                userEmail,
                product.id(),
                product.description(),
                request.quantity(),
                product.price()
        );

        return SaleResponse.from(saleRepository.save(sale));
    }

    public List<SaleResponse> findMySales() {
        String userEmail = (String) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();
        return saleRepository.findByUserEmail(userEmail)
                .stream().map(SaleResponse::from).toList();
    }


}
