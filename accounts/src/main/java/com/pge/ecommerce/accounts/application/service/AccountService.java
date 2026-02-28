package com.pge.ecommerce.accounts.application.service;

import com.pge.ecommerce.accounts.application.dto.LoginRequest;
import com.pge.ecommerce.accounts.application.dto.LoginResponse;
import com.pge.ecommerce.accounts.application.dto.RegisterRequest;
import com.pge.ecommerce.accounts.domain.model.Account;
import com.pge.ecommerce.accounts.domain.repository.AccountRepository;
import com.pge.ecommerce.accounts.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        String hash = passwordEncoder.encode(request.password());
        Account account = Account.create(request.email(), hash, request.address());
        accountRepository.save(account);
    }

    public LoginResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));


        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }


        String token = jwtUtil.generateToken(account.getEmail());
        return new LoginResponse(token, account.getEmail());
    }

}
