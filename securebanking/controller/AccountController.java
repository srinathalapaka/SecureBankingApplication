package com.vcube.securebanking.controller;

 
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.vcube.securebanking.dto.TransferRequest;
import com.vcube.securebanking.service.AccountService;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public String create(Authentication authentication) {
        return accountService.createAccount(authentication.getName());
    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        return accountService.transfer(request);
    }
}