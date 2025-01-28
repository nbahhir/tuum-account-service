package com.interview.test.tuumaccountservice.rabbitmq.events;

import com.interview.test.tuumaccountservice.entities.TransactionEntity;

public record TransactionCreatedEvent(String transactionId, String accountId, String amount,
    String amountAfter, String currency, String direction, String description) {

    public static TransactionCreatedEvent of(TransactionEntity transaction) {
        return new TransactionCreatedEvent(transaction.getTransactionId().toString(),
            transaction.getAccountId().toString(), transaction.getAmount().toString(),
            transaction.getBalanceAfterTransaction().toString(), transaction.getCurrency().toString(),
            transaction.getDirection().toString(), transaction.getDescription());
    }
}
