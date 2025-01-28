package com.interview.test.tuumaccountservice.services;

import com.interview.test.tuumaccountservice.dto.Account;
import com.interview.test.tuumaccountservice.dto.interfaces.BalanceDTO;
import com.interview.test.tuumaccountservice.dto.interfaces.CreateAccountRequest;
import com.interview.test.tuumaccountservice.dto.interfaces.AccountBalanceResponse;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.exceptions.EntryNotFoundException;
import com.interview.test.tuumaccountservice.services.core.AccountService;
import com.interview.test.tuumaccountservice.services.core.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountBalanceService {
    private final AccountService accountService;
    private final BalanceService balanceService;

    // These operations need to be done in transaction
    // We cannot create an account if the balance creation was unsuccessful
    @Transactional
    public AccountBalanceResponse createNewAccount(CreateAccountRequest request) {
        UUID customerId = UUID.fromString(request.getCustomerId());

        if (accountService.findAccountByCustomerId(customerId).isPresent()) {
            throw new IllegalArgumentException("This account already exists.");
        }

        UUID accountId = accountService.createAccount(customerId, request.getCountry());

        List<BalanceDTO> createdBalances = new ArrayList<>();
        // I assume the balance is set to 0 when the one is created? Seems logical.
        // Would be good to create new account with 10000 dollars already in though.
        // Also using distinct() not to create 8 different euro accounts
        request.getCurrencies().stream().distinct().forEach(currency -> {
            balanceService.createBalance(accountId, BalanceCurrency.of(currency));
            createdBalances.add(new BalanceDTO("0", currency));
        });
        return new AccountBalanceResponse(accountId.toString(), request.getCustomerId(), createdBalances);
    }

    public AccountBalanceResponse getAccountAndBalances(String rawAccountId) {
        UUID accountId = UUID.fromString(rawAccountId);

        // No sense in searching for balances if user doesn't exist
        Account account = accountService.findAccountByAccountId(accountId)
            .orElseThrow(() -> new EntryNotFoundException("Account not found."));

        List<BalanceDTO> balances = balanceService.findBalances(accountId)
                .stream().map(balance -> {
                    String amount = balance.amount().toString();
                    String currency = balance.currency().toString();
                    return new BalanceDTO(amount, currency);
                })
                .toList();
        return new AccountBalanceResponse(rawAccountId, account.customerId().toString(),
            balances);
    }
}
