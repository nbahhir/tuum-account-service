package com.interview.test.tuumaccountservice.dto.interfaces;

public record TransactionDTO(String transactionId, String amount, String currency,
     String direction, String description) {
}
