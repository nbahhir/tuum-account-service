package com.interview.test.tuumaccountservice.enums;

import java.util.Arrays;

public enum BalanceCurrency {
    EUR,
    SEK,
    GBP,
    USD;

    public static BalanceCurrency of(String rawCurrency) {
        return Arrays.stream(BalanceCurrency.values())
            .filter(currencyEnum -> currencyEnum.name().equals(rawCurrency))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid currency."));
    }
}
