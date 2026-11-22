package com.bank.bankingai.controller;

import com.bank.bankingai.dto.TransactionRequest;
import com.bank.bankingai.dto.TransactionResponse;
import com.bank.bankingai.entity.User;
import com.bank.bankingai.repository.UserRepository;
import com.bank.bankingai.service.TransactionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(
            TransactionService transactionService,
            UserRepository userRepository) {

        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        TransactionResponse response =
                transactionService.createTransaction(request, user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        List<TransactionResponse> transactions =
                transactionService.getTransactions(user);

        return ResponseEntity.ok(transactions);
    }

    private User getAuthenticatedUser(Authentication authentication) {

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }
    @PostMapping("/generate-dummy")
        public ResponseEntity<Map<String, Object>> generateDummyTransactions(
                Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        int count = transactionService.generateDummyTransactions(user);

        return ResponseEntity.ok(Map.of(
                "message", "Dummy transactions generated successfully",
                "count", count
        ));
        }
}
