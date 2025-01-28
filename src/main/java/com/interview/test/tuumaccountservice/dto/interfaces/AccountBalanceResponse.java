package com.interview.test.tuumaccountservice.dto.interfaces;

import java.util.List;

public record AccountBalanceResponse(String accountId, String customerId, List<BalanceDTO> balances) {
}
