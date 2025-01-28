package com.interview.test.tuumaccountservice.rabbitmq.events;

import com.interview.test.tuumaccountservice.entities.AccountEntity;

public record AccountCreatedEvent(String accountId, String customerId, String country) {
    public static AccountCreatedEvent of(AccountEntity account) {
        return new AccountCreatedEvent(account.getAccountId().toString(), account.getCustomerId().toString(),
            account.getCountry());
    }
}
