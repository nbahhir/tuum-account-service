package com.interview.test.tuumaccountservice.mybatis;

import com.interview.test.tuumaccountservice.entities.BalanceEntity;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@SpringBootTest
@Testcontainers
class BalanceMapperIntTest {

    @Autowired
    BalanceMapper balanceMapper;
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry props) {
        props.add("spring.datasource.url", postgres::getJdbcUrl);
        props.add("spring.datasource.username", postgres::getUsername);
        props.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testInsertAndFindWorks() {
        UUID testAccountId = UUID.randomUUID();
        UUID testBalanceId = UUID.randomUUID();
        BigDecimal testAmount = new BigDecimal("2.00");
        BalanceCurrency testCurrency = BalanceCurrency.GBP;

        var balanceCreated1 = new BalanceEntity();
        balanceCreated1.setAccountId(testAccountId);
        balanceCreated1.setAmount(testAmount);
        balanceCreated1.setCurrency(testCurrency);
        balanceCreated1.setBalanceId(testBalanceId);

        var balanceDifferent = new BalanceEntity(UUID.randomUUID(),
            BalanceCurrency.EUR, UUID.randomUUID(), BigDecimal.TWO);

        balanceMapper.insertBalance(balanceCreated1);
        balanceMapper.insertBalance(balanceDifferent);

        List<BalanceEntity> foundBalance = balanceMapper.findBalancesByAccountId(testAccountId);

        Assertions.assertEquals(1, foundBalance.size());
        Assertions.assertEquals(balanceCreated1, foundBalance.get(0));

        var balanceCreated2 = new BalanceEntity();
        balanceCreated2.setAccountId(testAccountId);
        balanceCreated2.setAmount(new BigDecimal("1000.00"));
        balanceCreated2.setCurrency(BalanceCurrency.GBP);
        balanceCreated2.setBalanceId(UUID.randomUUID());

        balanceMapper.insertBalance(balanceCreated2);

        foundBalance = balanceMapper.findBalancesByAccountId(testAccountId);

        Assertions.assertEquals(2, foundBalance.size());
        Assertions.assertTrue(foundBalance.contains(balanceCreated2));
    }

    @Test
    void testUpdateBalance() {
        UUID testAccountId = UUID.randomUUID();
        UUID testBalanceId = UUID.randomUUID();
        BigDecimal testAmount = new BigDecimal("2.00");
        BalanceCurrency testCurrency = BalanceCurrency.GBP;

        var balanceCreated1 = new BalanceEntity();
        balanceCreated1.setAccountId(testAccountId);
        balanceCreated1.setAmount(testAmount);
        balanceCreated1.setCurrency(testCurrency);
        balanceCreated1.setBalanceId(testBalanceId);

        var balanceDifferent = new BalanceEntity(UUID.randomUUID(),
                BalanceCurrency.EUR, UUID.randomUUID(), BigDecimal.TWO);
        balanceMapper.insertBalance(balanceCreated1);
        balanceMapper.insertBalance(balanceDifferent);

        BigDecimal newBalance = new BigDecimal("777.00");
        balanceMapper.updateBalance(balanceCreated1.getBalanceId(), new BigDecimal("777.00"));
        var foundBalance = balanceMapper.findBalancesByAccountId(testAccountId);

        Assertions.assertEquals(1, foundBalance.size());
        Assertions.assertEquals(newBalance, foundBalance.get(0).getAmount());

    }
}
