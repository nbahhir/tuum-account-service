package com.interview.test.tuumaccountservice.dto.interfaces;

import java.util.List;

public record GetTransactionsResponse(String accountId, List<TransactionDTO> transactions) {
}
