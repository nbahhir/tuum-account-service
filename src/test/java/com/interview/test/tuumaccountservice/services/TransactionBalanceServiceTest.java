package com.interview.test.tuumaccountservice.services;

import com.interview.test.tuumaccountservice.dto.Balance;
import com.interview.test.tuumaccountservice.dto.Transaction;
import com.interview.test.tuumaccountservice.dto.interfaces.CreateTransactionRequest;
import com.interview.test.tuumaccountservice.dto.interfaces.GetTransactionsResponse;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;
import com.interview.test.tuumaccountservice.exceptions.EntryNotFoundException;
import com.interview.test.tuumaccountservice.services.core.BalanceService;
import com.interview.test.tuumaccountservice.services.core.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TransactionBalanceServiceTest {

    @Mock
    BalanceService balanceServiceMock;
    @Mock
    TransactionService transactionServiceMock;

    @InjectMocks
    TransactionBalanceService transactionBalanceService;

    @Test
    void testNormalTransactionCreation() {
        String testRawAccountId = "b1faeabf-c2dc-49fe-bcf7-fd663b2e0124";
        UUID testAccountId = UUID.fromString(testRawAccountId);
        String testAmount = "12.60";
        String testCurrency = "USD";
        String direction = "IN";
        String description = "i am tired of typing unit tests, but i have duty...";
        CreateTransactionRequest request = new CreateTransactionRequest(testRawAccountId, testAmount,
            testCurrency, direction, description);

        Balance testBalance = new Balance(UUID.randomUUID(), testAccountId,
            BalanceCurrency.USD, BigDecimal.ONE);
        UUID testTransactionId = UUID.randomUUID();
        BigDecimal newAmount = testBalance.amount().add(new BigDecimal(testAmount));

        Mockito.when(balanceServiceMock.findBalances(testAccountId))
            .thenReturn(List.of(testBalance));

        Mockito.when(transactionServiceMock.createTransaction(testAccountId,
            new BigDecimal(testAmount),
            newAmount,
            testBalance.currency(),
            TransactionDirection.IN,
            description
            ))
            .thenReturn(testTransactionId);

        transactionBalanceService.createTransaction(request);

        Mockito.verify(balanceServiceMock).updateBalance(testBalance.balanceId(), newAmount);
    }

    @Test
    void testTransactionCreationMissingBalance() {
        String testRawAccountId = "b1faeabf-c2dc-49fe-bcf7-fd663b2e0124";
        UUID testAccountId = UUID.fromString(testRawAccountId);
        String testAmount = "12.60";
        String testCurrency = "USD";
        String direction = "IN";
        String description = "i am tired of typing unit tests, but i have duty...";
        CreateTransactionRequest request = new CreateTransactionRequest(testRawAccountId, testAmount,
            testCurrency, direction, description);


        Mockito.when(balanceServiceMock.findBalances(testAccountId))
            .thenReturn(List.of());

        Assertions.assertThrows(EntryNotFoundException.class,
            () -> transactionBalanceService.createTransaction(request));
    }

    @Test
    void testTransactionCreationWrongCurrency() {
        String testRawAccountId = "b1faeabf-c2dc-49fe-bcf7-fd663b2e0124";
        UUID testAccountId = UUID.fromString(testRawAccountId);
        String testAmount = "12.60";
        String testCurrency = "USD";
        String direction = "IN";
        String description = "i am tired of typing unit tests, but i have duty...";
        CreateTransactionRequest request = new CreateTransactionRequest(testRawAccountId, testAmount,
                testCurrency, direction, description);

        Balance testBalance = new Balance(UUID.randomUUID(), testAccountId,
                BalanceCurrency.EUR, BigDecimal.ONE);

        Mockito.when(balanceServiceMock.findBalances(testAccountId))
                .thenReturn(List.of(testBalance));

        Assertions.assertThrows(EntryNotFoundException.class,
            () -> transactionBalanceService.createTransaction(request));
    }

    @Test
    void testTransactionCreationInsufficientAmounts() {
        String testRawAccountId = "b1faeabf-c2dc-49fe-bcf7-fd663b2e0124";
        UUID testAccountId = UUID.fromString(testRawAccountId);
        String testAmount = "12.60";
        String testCurrency = "USD";
        String direction = "OUT";
        String description = "i am tired of typing unit tests, but i have duty...";
        CreateTransactionRequest request = new CreateTransactionRequest(testRawAccountId, testAmount,
                testCurrency, direction, description);

        Balance testBalance = new Balance(UUID.randomUUID(), testAccountId,
                BalanceCurrency.USD, BigDecimal.ONE);

        Mockito.when(balanceServiceMock.findBalances(testAccountId))
                .thenReturn(List.of(testBalance));

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> transactionBalanceService.createTransaction(request));
    }

    @Test
    void testTransactionCreationNegativeAmount() {
        String testRawAccountId = "b1faeabf-c2dc-49fe-bcf7-fd663b2e0124";
        String testAmount = "-12.60";
        String testCurrency = "USD";
        String direction = "IN";
        String description = "i am tired of typing unit tests, but i have duty...";
        CreateTransactionRequest request = new CreateTransactionRequest(testRawAccountId, testAmount,
                testCurrency, direction, description);

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> transactionBalanceService.createTransaction(request));
    }

    @Test
    void testGetTransactions() {
        String testRawAccountId = "b1faeabf-c2dc-49fe-bcf7-fd663b2e0124";
        UUID testAccountId = UUID.fromString(testRawAccountId);
        BigDecimal testAmount = new BigDecimal("77.12");
        BigDecimal testBalanceAfter = new BigDecimal("100.00");
        BalanceCurrency testBalanceCurrency = BalanceCurrency.EUR;
        TransactionDirection testDirection = TransactionDirection.IN;
        String testDescription = "tsjfksjfksdjfk";

        Transaction transaction1 = new Transaction(UUID.randomUUID(), testAccountId,
            testAmount, testBalanceAfter, testBalanceCurrency, testDirection, testDescription);
        Transaction transaction2 = new Transaction(UUID.randomUUID(), testAccountId,
                testAmount, testBalanceAfter, testBalanceCurrency, testDirection, testDescription);

        Mockito.when(transactionServiceMock.findTransactions(testAccountId))
                        .thenReturn(List.of(transaction1, transaction2));

        GetTransactionsResponse response = transactionBalanceService.getAllTransaction(testRawAccountId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.transactions().size());
        Assertions.assertEquals(testRawAccountId, response.accountId());
    }

    @Test
    void testGetTransactionsNoAccount() {
        String testRawAccountId = "b1faeabf-c2dc-49fe-bcf7-fd663b2e0124";
        UUID testAccountId = UUID.fromString(testRawAccountId);

        Mockito.when(transactionServiceMock.findTransactions(testAccountId))
            .thenReturn(List.of());

        Assertions.assertThrows(EntryNotFoundException.class, () ->
            transactionBalanceService.getAllTransaction(testRawAccountId));
    }
}
