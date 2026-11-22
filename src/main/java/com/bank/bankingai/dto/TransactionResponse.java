package com.bank.bankingai.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private Long accountId;
    private String transactionType;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDateTime transactionDate;

    public TransactionResponse(
            Long id,
            Long accountId,
            String transactionType,
            BigDecimal amount,
            String category,
            String description,
            LocalDateTime transactionDate) {

        this.id = id;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}
