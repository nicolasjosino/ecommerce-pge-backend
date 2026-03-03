package com.pge.ecommerce.sales.infrastructure.persistence;

import com.pge.ecommerce.sales.domain.model.Sale;
import com.pge.ecommerce.sales.domain.repository.SaleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSaleRepository
        extends JpaRepository<Sale, Long>, SaleRepository {
    List<Sale> findByUserEmail(String userEmail);
}
