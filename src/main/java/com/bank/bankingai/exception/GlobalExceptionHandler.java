
package com.bank.bankingai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException exception) {

        String message = exception.getMessage();

        HttpStatus status = "Insufficient balance".equals(message)
                ? HttpStatus.BAD_REQUEST
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(Map.of("message", message));
    }
}
