package com.pge.ecommerce.accounts.domain.repository;

import com.pge.ecommerce.accounts.domain.model.Account;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
}
