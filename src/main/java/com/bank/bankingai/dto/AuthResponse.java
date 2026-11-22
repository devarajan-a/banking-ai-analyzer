
package com.bank.bankingai.dto;

public class AuthResponse {

    private String token;
    private String message;
    private String fullName;

    public AuthResponse(String token, String message, String fullName) {
        this.token = token;
        this.message = message;
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public String getFullName() {
        return fullName;
    }
}
