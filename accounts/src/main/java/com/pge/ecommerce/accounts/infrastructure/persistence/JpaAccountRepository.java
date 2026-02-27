package com.pge.ecommerce.accounts.infrastructure.persistence;

import com.pge.ecommerce.accounts.domain.model.Account;
import com.pge.ecommerce.accounts.domain.repository.AccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAccountRepository extends JpaRepository<Account, Long>, AccountRepository {
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
}
