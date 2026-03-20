package com.vcube.securebanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vcube.securebanking.model.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
}