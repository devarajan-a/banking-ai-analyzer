package com.bank.bankingai.service;

import com.bank.bankingai.dto.AccountResponse;
import com.bank.bankingai.entity.Account;
import com.bank.bankingai.entity.User;
import com.bank.bankingai.repository.AccountRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createDefaultAccount(User user) {

        Account account = new Account();

        account.setAccountNumber(generateAccountNumber());
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        return accountRepository.save(account);
    }

    private String generateAccountNumber() {

        String accountNumber;

        do {
            StringBuilder number = new StringBuilder();

            for (int i = 0; i < 12; i++) {
                number.append(secureRandom.nextInt(10));
            }

            accountNumber = number.toString();

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    public List<AccountResponse> getAccountsByUser(User user) {

        return accountRepository.findByUser(user)
                .stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getBalance()
                ))
                .toList();
    }
}