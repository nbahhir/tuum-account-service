package com.interview.test.tuumaccountservice.entities;

import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
    UUID transactionId;
    UUID accountId;
    BigDecimal amount;
    BigDecimal balanceAfterTransaction;
    BalanceCurrency currency;
    TransactionDirection direction;
    String description;
}
