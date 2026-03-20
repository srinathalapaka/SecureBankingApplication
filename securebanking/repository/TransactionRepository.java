package com.vcube.securebanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vcube.securebanking.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}