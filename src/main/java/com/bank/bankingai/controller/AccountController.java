package com.bank.bankingai.controller;

import com.bank.bankingai.dto.AccountResponse;
import com.bank.bankingai.entity.User;
import com.bank.bankingai.repository.UserRepository;
import com.bank.bankingai.service.AccountService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    public AccountController(
            AccountService accountService,
            UserRepository userRepository) {

        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        List<AccountResponse> accounts =
                accountService.getAccountsByUser(user);

        return ResponseEntity.ok(accounts);
    }
}