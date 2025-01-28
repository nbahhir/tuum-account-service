package com.interview.test.tuumaccountservice.services;

import com.interview.test.tuumaccountservice.dto.Balance;
import com.interview.test.tuumaccountservice.dto.Transaction;
import com.interview.test.tuumaccountservice.dto.interfaces.CreateTransactionRequest;
import com.interview.test.tuumaccountservice.dto.interfaces.GetTransactionsResponse;
import com.interview.test.tuumaccountservice.dto.interfaces.TransactionCreationResponse;
import com.interview.test.tuumaccountservice.dto.interfaces.TransactionDTO;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;
import com.interview.test.tuumaccountservice.exceptions.EntryNotFoundException;
import com.interview.test.tuumaccountservice.services.core.BalanceService;
import com.interview.test.tuumaccountservice.services.core.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionBalanceService {

    private final BalanceService balanceService;
    private final TransactionService transactionService;

    @Transactional
    public TransactionCreationResponse createTransaction(CreateTransactionRequest request) {
        UUID accountId = UUID.fromString(request.getAccountId());
        BalanceCurrency requestCurrency = BalanceCurrency.of(request.getCurrency());
        TransactionDirection requestDirection = TransactionDirection.of(request.getDirection());
        BigDecimal requestAmount = validateAmount(request.getAmount());

        List<Balance> balances = balanceService.findBalances(accountId);

        if (balances.isEmpty()) {
            throw new EntryNotFoundException("Account missing balances.");
        }

        Balance balance = balances.stream()
            .filter(foundBalance -> foundBalance.currency().equals(requestCurrency))
            .findFirst()
            .orElseThrow(() -> new EntryNotFoundException("Currency not found for this user."));

        validateSufficientFunds(requestDirection, balance.amount(), requestAmount);

        BigDecimal newBalanceAmount = requestDirection.equals(TransactionDirection.IN) ?
            balance.amount().add(requestAmount) : balance.amount().subtract(requestAmount);

        UUID transactionId = transactionService.createTransaction(accountId, requestAmount,
            newBalanceAmount, requestCurrency, requestDirection, request.getDescription());

        balanceService.updateBalance(balance.balanceId(), newBalanceAmount);

        return new TransactionCreationResponse(request.getAccountId(),
            transactionId.toString(), request.getAmount(), request.getCurrency(),
            request.getDirection(), request.getDescription(), newBalanceAmount.toString());
    }

    public GetTransactionsResponse getAllTransaction(String rawAccountId) {
        UUID accountId = UUID.fromString(rawAccountId);
        List<Transaction> transactions = transactionService.findTransactions(accountId);

        if (transactions.isEmpty()) {
            throw new EntryNotFoundException("Invalid account.");
        }

        List<TransactionDTO> transactionDTOS = transactions.stream().map(transaction -> {
            String transactionId = transaction.transactionId().toString();
            String amount = transaction.amount().toString();
            String currency = transaction.currency().toString();
            String direction = transaction.direction().toString();
            String description = transaction.description();
            return new TransactionDTO(transactionId, amount, currency, direction,
                description);
        })
        .toList();
        return new GetTransactionsResponse(rawAccountId, transactionDTOS);
    }

    // There already exists a validation in DTO, but decided to keep it just in case
    private BigDecimal validateAmount(String amount) {
        try {
            BigDecimal bigDecimal = new BigDecimal(amount);
            if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Amount cannot be less than 0.");
            }
            return new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount.");
        }
    }

    private void validateSufficientFunds(TransactionDirection direction, BigDecimal available,
        BigDecimal amount) {
        if (direction.equals(TransactionDirection.OUT) && available.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
    }
}
