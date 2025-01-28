package com.interview.test.tuumaccountservice.entities;

import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceEntity {
    UUID balanceId;
    BalanceCurrency currency;
    UUID accountId;
    BigDecimal amount; // Using BigDecimal for good precision, as we deal with financial stuff.
}
