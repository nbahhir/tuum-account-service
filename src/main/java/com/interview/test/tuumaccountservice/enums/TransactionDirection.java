package com.interview.test.tuumaccountservice.enums;

import java.util.Arrays;

public enum TransactionDirection {
    IN,
    OUT;

    public static TransactionDirection of(String rawDirection) {
        return Arrays.stream(TransactionDirection.values())
            .filter(direction -> direction.name().equals(rawDirection))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid direction."));
    }
}
