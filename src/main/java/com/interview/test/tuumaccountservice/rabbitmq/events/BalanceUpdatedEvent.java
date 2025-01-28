package com.interview.test.tuumaccountservice.rabbitmq.events;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceUpdatedEvent(String balanceId, String amount) {
    public static BalanceUpdatedEvent of(UUID balanceId, BigDecimal amount) {
        return new BalanceUpdatedEvent(balanceId.toString(), amount.toString());
    }
}
