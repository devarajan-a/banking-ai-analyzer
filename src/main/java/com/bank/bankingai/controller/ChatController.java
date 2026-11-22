
package com.bank.bankingai.controller;

import com.bank.bankingai.dto.ChatRequest;
import com.bank.bankingai.dto.ChatResponse;
import com.bank.bankingai.entity.User;
import com.bank.bankingai.repository.UserRepository;
import com.bank.bankingai.service.AccountService;
import com.bank.bankingai.service.GroqService;
import com.bank.bankingai.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final GroqService groqService;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final UserRepository userRepository;

    public ChatController(
            GroqService groqService,
            TransactionService transactionService,
            AccountService accountService,
            UserRepository userRepository) {

        this.groqService = groqService;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.userRepository = userRepository;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(
            @RequestBody ChatRequest request,
            Authentication authentication) {

        if (request.getQuestion() == null ||
                request.getQuestion().isBlank()) {

            throw new RuntimeException(
                    "Question cannot be empty"
            );
        }

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        var accounts =
                accountService.getAccountsByUser(user);

        var transactions =
                transactionService.getTransactions(user);

        String reply = groqService.generateReply(
                request.getQuestion(),
                accounts,
                transactions
        );

        return ResponseEntity.ok(
                new ChatResponse(reply)
        );
    }
}
