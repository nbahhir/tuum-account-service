package com.interview.test.tuumaccountservice.dto;

import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;

import java.math.BigDecimal;
import java.util.UUID;

public record Transaction(UUID transactionId,
      UUID accountId,
      BigDecimal amount,
      BigDecimal balanceAfterTransaction,
      BalanceCurrency currency,
      TransactionDirection direction,
      String description) {
}
