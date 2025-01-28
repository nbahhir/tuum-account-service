package com.interview.test.tuumaccountservice.rabbitmq.events;

import com.interview.test.tuumaccountservice.entities.BalanceEntity;

public record BalanceCreatedEvent(String balanceId, String accountId, String currency)  {
    public static BalanceCreatedEvent of(BalanceEntity balance) {
        return new BalanceCreatedEvent(balance.getBalanceId().toString(),
        balance.getAccountId().toString(), balance.getCurrency().toString());
    }
}
