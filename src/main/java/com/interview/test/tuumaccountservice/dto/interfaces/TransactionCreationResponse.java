package com.interview.test.tuumaccountservice.dto.interfaces;

public record TransactionCreationResponse(
    String accountId,
    String transactionId,
    String amount,
    String currency,
    String direction,
    String description,
    String balanceAfterTransaction
) {
}
