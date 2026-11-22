package com.bank.bankingai.controller;

import com.bank.bankingai.dto.AuthResponse;
import com.bank.bankingai.dto.LoginRequest;
import com.bank.bankingai.dto.SignupRequest;
import com.bank.bankingai.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @RequestBody SignupRequest request) {

        String response = authService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        AuthResponse response = authService.loginUser(request);

        return ResponseEntity.ok(response);
    }
}