package com.interview.test.tuumaccountservice.dto;

import com.interview.test.tuumaccountservice.enums.BalanceCurrency;

import java.math.BigDecimal;
import java.util.UUID;

public record Balance(UUID balanceId, UUID accountId,
    BalanceCurrency currency, BigDecimal amount) {
}
