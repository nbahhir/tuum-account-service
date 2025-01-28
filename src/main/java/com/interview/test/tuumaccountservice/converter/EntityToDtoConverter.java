package com.interview.test.tuumaccountservice.converter;

import com.interview.test.tuumaccountservice.dto.Account;
import com.interview.test.tuumaccountservice.dto.Balance;
import com.interview.test.tuumaccountservice.dto.Transaction;
import com.interview.test.tuumaccountservice.entities.AccountEntity;
import com.interview.test.tuumaccountservice.entities.BalanceEntity;
import com.interview.test.tuumaccountservice.entities.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityToDtoConverter {
    public Account convertAccount(AccountEntity accountEntity) {
        return new Account(accountEntity.getCustomerId(), accountEntity.getAccountId(),
            accountEntity.getCountry());
    }

    public Balance convertBalance(BalanceEntity balanceEntity) {
        return new Balance(balanceEntity.getBalanceId(), balanceEntity.getAccountId(),
            balanceEntity.getCurrency(), balanceEntity.getAmount());
    }

    public Transaction convertTransaction(TransactionEntity transactionEntity) {
        return new Transaction(transactionEntity.getTransactionId(), transactionEntity.getAccountId(),
            transactionEntity.getAmount(), transactionEntity.getBalanceAfterTransaction(),
            transactionEntity.getCurrency(), transactionEntity.getDirection(),
            transactionEntity.getDescription());
    }
}
