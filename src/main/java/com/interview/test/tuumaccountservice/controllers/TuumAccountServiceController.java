package com.interview.test.tuumaccountservice.controllers;

import com.interview.test.tuumaccountservice.dto.interfaces.*;
import com.interview.test.tuumaccountservice.services.AccountBalanceService;
import com.interview.test.tuumaccountservice.services.TransactionBalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class TuumAccountServiceController {

    private final AccountBalanceService accountBalanceService;
    private final TransactionBalanceService transactionBalanceService;

    @PostMapping("/accounts/create")
    public ResponseEntity<AccountBalanceResponse> createAccount(
        @RequestBody @Valid CreateAccountRequest request) {
        AccountBalanceResponse response = accountBalanceService.createNewAccount(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountBalanceResponse> getAccount(@PathVariable String accountId) {
        AccountBalanceResponse response = accountBalanceService.getAccountAndBalances(accountId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transactions/create")
    public ResponseEntity<TransactionCreationResponse> createTransaction(@RequestBody @Valid CreateTransactionRequest request) {
        TransactionCreationResponse response = transactionBalanceService.createTransaction(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/{accountId}")
    public ResponseEntity<GetTransactionsResponse> getTransactions(@PathVariable String accountId) {
        GetTransactionsResponse response = transactionBalanceService.getAllTransaction(accountId);
        return ResponseEntity.ok(response);
    }

}
