
package com.bank.bankingai.service;

import com.bank.bankingai.dto.AccountResponse;
import com.bank.bankingai.dto.TransactionResponse;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private final RestClient groqRestClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.model}")
    private String groqModel;

    public GroqService(
            RestClient groqRestClient,
            ObjectMapper objectMapper) {

        this.groqRestClient = groqRestClient;
        this.objectMapper = objectMapper;
    }

    public String generateReply(
            String question,
            List<AccountResponse> accounts,
            List<TransactionResponse> transactions) {

        try {

            // Exclude full account numbers from AI requests.
            List<Map<String, Object>> safeAccounts =
                    accounts.stream()
                            .map(account -> Map.<String, Object>of(
                                    "accountId", account.getId(),
                                    "accountType", account.getAccountType(),
                                    "balance", account.getBalance()
                            ))
                            .toList();

            String accountData =
                    objectMapper.writeValueAsString(safeAccounts);

            String transactionData =
                    objectMapper.writeValueAsString(transactions);

            String prompt = """
                    You are a concise Banking AI Assistant.

                    Account information from the database:
                    %s

                    Transaction history:
                    %s

                    User question:
                    %s

                    Response rules:
                    1. Answer exactly what the user asks.
                    2. Keep responses short, clear, and direct.
                    3. Do not include unnecessary introductions or greetings.
                    4. Do not repeat the user's question.
                    5. Do not show transaction tables unless explicitly requested.
                    6. Use the account balance provided in the account information
                       as the actual current balance.
                    7. Never calculate or replace the actual account balance
                       using transaction history.
                    8. Calculate deposits and withdrawals using the transaction
                       history when requested.
                    9. Never invent transactions or financial information.
                    10. If no transactions match, say so in one short sentence.
                    11. If the user asks for account details, provide account ID,
                        account type, and actual current balance.
                    12. Never reveal full account numbers.
                    13. If the question is ambiguous, ask one short clarification question.
                    14. All amounts are in Indian Rupees. Always use ₹, never $.
                    15. Display dates exactly as provided.
                    16. Use simple language and avoid unnecessary explanations.
                    """.formatted(accountData, transactionData, question);

            Map<String, Object> requestBody = Map.of(
                    "model", groqModel,
                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content", "You are a banking account and transaction analysis assistant."
                            ),
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    ),
                    "temperature", 0.2
            );

            JsonNode response = groqRestClient.post()
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                throw new RuntimeException(
                        "Empty response received from Groq AI"
                );
            }

            String reply = response
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (reply.isBlank()) {
                throw new RuntimeException(
                        "Groq AI returned an empty reply"
                );
            }

            return reply;

        } catch (JacksonException exception) {

            throw new RuntimeException(
                    "Unable to process banking data",
                    exception
            );
        }
    }
}
