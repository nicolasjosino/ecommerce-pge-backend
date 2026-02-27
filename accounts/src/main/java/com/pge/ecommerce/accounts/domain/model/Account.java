package com.pge.ecommerce.accounts.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String address;

    public static Account create(String email, String passwordHash, String address) {
        Account account = new Account();
        account.email = email;
        account.passwordHash = passwordHash;
        account.address = address;
        return account;
    }


}
