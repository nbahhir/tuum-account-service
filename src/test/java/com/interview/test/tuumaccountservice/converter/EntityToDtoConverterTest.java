package com.interview.test.tuumaccountservice.converter;

import com.interview.test.tuumaccountservice.dto.Account;
import com.interview.test.tuumaccountservice.dto.Balance;
import com.interview.test.tuumaccountservice.dto.Transaction;
import com.interview.test.tuumaccountservice.entities.AccountEntity;
import com.interview.test.tuumaccountservice.entities.BalanceEntity;
import com.interview.test.tuumaccountservice.entities.TransactionEntity;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class EntityToDtoConverterTest {
    private final EntityToDtoConverter converter = new EntityToDtoConverter();

    @Test
    void testAccountConversion() {
        Account testAccount = new Account(UUID.randomUUID(),
            UUID.randomUUID(), "Circle K Hotdog (cabanos)");
        AccountEntity testEntity = new AccountEntity(testAccount.accountId(), testAccount.customerId(),
            testAccount.country());

        Account resultAccount = converter.convertAccount(testEntity);

        Assertions.assertEquals(testAccount, resultAccount);

    }

    @Test
    void testBalanceConversion() {
        Balance testBalance = new Balance(UUID.randomUUID(),
            UUID.randomUUID(), BalanceCurrency.USD, BigDecimal.ONE);
        BalanceEntity testEntity = new BalanceEntity(testBalance.balanceId(),
            testBalance.currency(), testBalance.accountId(), testBalance.amount());

        Balance resultBalance = converter.convertBalance(testEntity);

        Assertions.assertEquals(testBalance, resultBalance);
    }

    @Test
    void testTransactionConversion() {
        Transaction testTransaction = new Transaction(UUID.randomUUID(),
            UUID.randomUUID(), BigDecimal.ONE, BigDecimal.TWO, BalanceCurrency.USD,
            TransactionDirection.IN, "description");
        TransactionEntity testEntity = new TransactionEntity(testTransaction.transactionId(),
            testTransaction.accountId(), testTransaction.amount(), testTransaction.balanceAfterTransaction(),
            testTransaction.currency(), testTransaction.direction(), testTransaction.description());

        Transaction resultTransaction = converter.convertTransaction(testEntity);

        Assertions.assertEquals(testTransaction, resultTransaction);
    }
}
