package com.vcube.securebanking.service;
 
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.securebanking.dto.TransferRequest;
import com.vcube.securebanking.model.Account;
import com.vcube.securebanking.model.Transaction;
import com.vcube.securebanking.model.User;
import com.vcube.securebanking.repository.AccountRepository;
import com.vcube.securebanking.repository.TransactionRepository;
import com.vcube.securebanking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;
    private final UserRepository userRepo;

    // CREATE ACCOUNT
    public String createAccount(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setBalance(0.0);
        account.setUser(user);

        accountRepo.save(account);
        return "Account Created Successfully";
    }

    // TRANSFER MONEY
    @Transactional
    public String transfer(TransferRequest request) {

        Account from = accountRepo.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account to = accountRepo.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if(from.getBalance() < request.getAmount())
            throw new RuntimeException("Insufficient Balance");

        // FRAUD DETECTION RULE
        if(request.getAmount() > 100000)
            throw new RuntimeException("Fraud Alert: Large transaction blocked");

        from.setBalance(from.getBalance() - request.getAmount());
        to.setBalance(to.getBalance() + request.getAmount());

        accountRepo.save(from);
        accountRepo.save(to);

        // Save Transactions
        Transaction debit = new Transaction();
        debit.setAccount(from);
        debit.setAmount(request.getAmount());
        debit.setType("DEBIT");
        debit.setTimestamp(LocalDateTime.now());

        Transaction credit = new Transaction();
        credit.setAccount(to);
        credit.setAmount(request.getAmount());
        credit.setType("CREDIT");
        credit.setTimestamp(LocalDateTime.now());

        transactionRepo.save(debit);
        transactionRepo.save(credit);

        return "Transfer Successful";
    }
}