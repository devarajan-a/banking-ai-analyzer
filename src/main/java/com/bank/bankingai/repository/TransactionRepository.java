package com.bank.bankingai.repository;

import com.bank.bankingai.entity.Account;
import com.bank.bankingai.entity.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountOrderByTransactionDateDesc(
            Account account
    );
}