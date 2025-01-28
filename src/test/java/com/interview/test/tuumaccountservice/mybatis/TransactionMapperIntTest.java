package com.interview.test.tuumaccountservice.mybatis;

import com.interview.test.tuumaccountservice.entities.TransactionEntity;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;


@SpringBootTest
@Testcontainers
class TransactionMapperIntTest {

    @Autowired
    TransactionMapper transactionMapper;
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry props) {
        props.add("spring.datasource.url", postgres::getJdbcUrl);
        props.add("spring.datasource.username", postgres::getUsername);
        props.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void test() {
        UUID uuid = UUID.randomUUID();
        var transactionCreated1 = new TransactionEntity();
        transactionCreated1.setAccountId(uuid);
        transactionCreated1.setTransactionId(UUID.randomUUID());
        transactionCreated1.setAmount(BigDecimal.valueOf(500));
        transactionCreated1.setCurrency(BalanceCurrency.USD);
        transactionCreated1.setBalanceAfterTransaction(BigDecimal.valueOf(500));
        transactionCreated1.setDirection(TransactionDirection.OUT);

        var transactionCreated2 = new TransactionEntity();
        transactionCreated2.setAccountId(uuid);
        transactionCreated2.setTransactionId(UUID.randomUUID());
        transactionCreated2.setAmount(BigDecimal.valueOf(500));
        transactionCreated2.setCurrency(BalanceCurrency.EUR);
        transactionCreated2.setBalanceAfterTransaction(BigDecimal.valueOf(500));
        transactionCreated2.setDirection(TransactionDirection.IN);

        transactionMapper.insertTransaction(transactionCreated1);
        transactionMapper.insertTransaction(transactionCreated2);

        var transaction = transactionMapper.findTransactionsByAccountId(uuid);

    }
}
