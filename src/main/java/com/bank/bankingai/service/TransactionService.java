package com.bank.bankingai.service;

import com.bank.bankingai.dto.TransactionRequest;
import com.bank.bankingai.dto.TransactionResponse;
import com.bank.bankingai.entity.Account;
import com.bank.bankingai.entity.Transaction;
import com.bank.bankingai.entity.User;
import com.bank.bankingai.repository.AccountRepository;
import com.bank.bankingai.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(
            TransactionRequest request,
            User user) {

        Account account = accountRepository
                .findByIdAndUser(request.getAccountId(), user)
                .orElseThrow(() ->
                        new RuntimeException("Account not found")
                );

        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Transaction amount must be greater than zero"
            );
        }

        String type = request.getTransactionType();

        if (type == null) {
            throw new RuntimeException(
                    "Transaction type is required"
            );
        }

        type = type.trim().toUpperCase();

        if (type.equals("DEPOSIT")) {

            account.setBalance(account.getBalance().add(amount));

        } else if (type.equals("WITHDRAWAL")) {

            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            account.setBalance(account.getBalance().subtract(amount));

        } else {
            throw new RuntimeException(
                    "Invalid transaction type. Use DEPOSIT or WITHDRAWAL"
            );
        }

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAccount(account);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return new TransactionResponse(
                savedTransaction.getId(),
                account.getId(),
                savedTransaction.getTransactionType(),
                savedTransaction.getAmount(),
                savedTransaction.getCategory(),
                savedTransaction.getDescription(),
                savedTransaction.getTransactionDate()
        );
    }

    public List<TransactionResponse> getTransactions(User user) {

        return accountRepository.findByUser(user)
                .stream()
                .flatMap(account ->
                        transactionRepository
                                .findByAccountOrderByTransactionDateDesc(account)
                                .stream()
                )
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getAccount().getId(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getCategory(),
                        transaction.getDescription(),
                        transaction.getTransactionDate()
                ))
                .toList();
    }

    @Transactional
        public int generateDummyTransactions(User user) {

        Account account = accountRepository.findByUser(user)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (transactionRepository.existsByAccountAndDescriptionStartingWith(
                account, "DUMMY - ")) {
                throw new RuntimeException(
                        "Dummy transactions have already been generated"
                );
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMonths(6);

        long seconds = ChronoUnit.SECONDS.between(start, now);

        String[] depositCategories = {
                "SALARY", "FREELANCE", "REFUND", "CASHBACK", "INTEREST"
        };

        String[] withdrawalCategories = {
                "GROCERIES", "FOOD", "TRANSPORT", "UTILITIES",
                "SHOPPING", "HEALTHCARE", "ENTERTAINMENT",
                "EDUCATION", "RENT", "CASH"
        };

        List<Transaction> transactions = new ArrayList<>();

        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;

        for (int i = 0; i < 500; i++) {

                boolean deposit = i < 100;

                Transaction transaction = new Transaction();

                transaction.setAccount(account);

                BigDecimal amount;

                if (deposit) {
                amount = BigDecimal.valueOf(
                        random.nextInt(5000, 15001)
                );

                transaction.setTransactionType("DEPOSIT");

                String category = depositCategories[
                        random.nextInt(depositCategories.length)
                ];

                transaction.setCategory(category);
                transaction.setDescription(
                        "DUMMY - " + category + " income"
                );

                totalDeposits = totalDeposits.add(amount);

                } else {
                amount = BigDecimal.valueOf(
                        random.nextInt(100, 1001)
                );

                transaction.setTransactionType("WITHDRAWAL");

                String category = withdrawalCategories[
                        random.nextInt(withdrawalCategories.length)
                ];

                transaction.setCategory(category);
                transaction.setDescription(
                        "DUMMY - " + category + " expense"
                );

                totalWithdrawals = totalWithdrawals.add(amount);
                }

                transaction.setAmount(amount);

                transaction.setTransactionDate(
                        start.plusSeconds(random.nextLong(seconds + 1))
                );

                transactions.add(transaction);
        }

        account.setBalance(
                account.getBalance()
                        .add(totalDeposits)
                        .subtract(totalWithdrawals)
        );

        accountRepository.save(account);

        transactionRepository.saveAll(transactions);

        return transactions.size();
        }
}