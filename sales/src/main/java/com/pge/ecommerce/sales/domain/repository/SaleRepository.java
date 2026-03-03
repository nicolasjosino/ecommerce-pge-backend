package com.pge.ecommerce.sales.domain.repository;

import com.pge.ecommerce.sales.domain.model.Sale;

import java.util.List;

public interface SaleRepository {
    Sale save(Sale sale);

    List<Sale> findByUserEmail(String userEmail);

}
