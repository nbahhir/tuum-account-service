package com.interview.test.tuumaccountservice.controllers;

import com.interview.test.tuumaccountservice.entities.AccountEntity;
import com.interview.test.tuumaccountservice.entities.BalanceEntity;
import com.interview.test.tuumaccountservice.entities.TransactionEntity;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;
import com.interview.test.tuumaccountservice.mybatis.AccountMapper;
import com.interview.test.tuumaccountservice.mybatis.BalanceMapper;
import com.interview.test.tuumaccountservice.mybatis.TransactionMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TuumAccountServiceControllerIntTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry props) {
        props.add("spring.datasource.url", postgres::getJdbcUrl);
        props.add("spring.datasource.username", postgres::getUsername);
        props.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    BalanceMapper balanceMapper;

    @Autowired
    TransactionMapper transactionMapper;

    @Test
    void testCreateAccountAndBalance() throws Exception {
        mockMvc.perform(post("/api/accounts/create")
                .contentType("application/json")
                .content("""
                    {
                        "customerId": "40109fe2-5bd6-41f6-b85c-0dd5b6ab8dde",
                        "country": "Estonia",
                        "currencies": ["USD", "EUR"]
                    }
                """))
                .andExpect(status().isOk());

        UUID customerId = UUID.fromString("40109fe2-5bd6-41f6-b85c-0dd5b6ab8dde");

        AccountEntity databaseAccount = accountMapper
            .findAccountByCustomerId(customerId);
        Assertions.assertEquals(customerId, databaseAccount.getCustomerId());
        Assertions.assertEquals("Estonia", databaseAccount.getCountry());

        List<BalanceEntity> balances = balanceMapper.findBalancesByAccountId(databaseAccount.getAccountId());

        Assertions.assertEquals(2, balances.size());
        Assertions.assertEquals(List.of("USD", "EUR"), balances.stream()
                .map(BalanceEntity::getCurrency)
                .map(BalanceCurrency::toString)
                .toList()
        );
        Assertions.assertEquals(List.of("0.00", "0.00"), balances.stream()
                .map(BalanceEntity::getAmount)
                .map(BigDecimal::toString)
                .toList()
        );
    }

    @Test
    void testCreateAccountAlreadyExists() throws Exception {
        UUID customerId = UUID.fromString("40109fe2-5bd6-41f6-b85c-0dd5b6ab8dde");

        AccountEntity accountEntity = new AccountEntity(UUID.randomUUID(), customerId,
                "Estonia");
        accountMapper.insertAccount(accountEntity);

        mockMvc.perform(post("/api/accounts/create")
                        .contentType("application/json")
                        .content("""
                    {
                        "customerId": "40109fe2-5bd6-41f6-b85c-0dd5b6ab8dde",
                        "country": "Estonia",
                        "currencies": ["USD", "EUR"]
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAccountWrongCurrencies() throws Exception {
        mockMvc.perform(post("/api/accounts/create")
                        .contentType("application/json")
                        .content("""
                    {
                        "customerId": "40109fe2-5bd6-41f6-b85c-0dd5b6ab8dde",
                        "country": "Estonia",
                        "currencies": ["HELP", "EUR"]
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAccountMissingInfo() throws Exception {
        mockMvc.perform(post("/api/accounts/create")
                        .contentType("application/json")
                        .content("""
                    {
                        "customerId": "40109fe2-5bd6-41f6-b85c-0dd5b6ab8dde",
                        "currencies": ["HELP", "EUR"]
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAccounts() throws Exception {
        UUID customerId = UUID.fromString("40109fe2-5bd6-41f6-b85c-0dd5b6ab8dde");
        UUID accountId = UUID.fromString("10f73d86-21d7-4116-85de-33d9583c2d23");
        AccountEntity accountEntity = new AccountEntity(accountId, customerId,
            "Estonia");
        BalanceEntity balanceEntity = new BalanceEntity(UUID.randomUUID(), BalanceCurrency.USD,
            accountEntity.getAccountId(), new BigDecimal("12.00"));

        accountMapper.insertAccount(accountEntity);
        balanceMapper.insertBalance(balanceEntity);

        mockMvc.perform(get("/api/accounts/10f73d86-21d7-4116-85de-33d9583c2d23")
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(customerId.toString()))
            .andExpect(jsonPath("$.accountId").value(accountId.toString()))
            .andExpect(jsonPath("$.balances[0].currency").value("USD"))
            .andExpect(jsonPath("$.balances[0].amount").value("12.00"));
    }

    @Test
    void testCreateTransaction() throws Exception {
        UUID accountId = UUID.fromString("10f73d86-21d7-4116-85de-33d9583c2d23");
        BalanceEntity balanceEntity = new BalanceEntity(UUID.randomUUID(), BalanceCurrency.USD,
                accountId, new BigDecimal("12.00"));

        balanceMapper.insertBalance(balanceEntity);

        mockMvc.perform(post("/api/transactions/create")
                        .contentType("application/json")
                        .content("""
                    {
                         "accountId":"10f73d86-21d7-4116-85de-33d9583c2d23",
                         "amount": 5.43,
                         "currency": "USD",
                         "direction": "OUT",
                         "description": "coca cola .0."
                     }
                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/transactions/create")
                        .contentType("application/json")
                        .content("""
                    {
                         "accountId":"10f73d86-21d7-4116-85de-33d9583c2d23",
                         "amount": 1.09,
                         "currency": "USD",
                         "direction": "OUT",
                         "description": "coca cola 2 i will drink everything"
                     }
                """))
                .andExpect(status().isOk());

        List<TransactionEntity> databaseTransactions =
            transactionMapper.findTransactionsByAccountId(UUID.fromString("10f73d86-21d7-4116-85de-33d9583c2d23"));

        Assertions.assertEquals(2, databaseTransactions.size());
        Assertions.assertEquals(BalanceCurrency.USD, databaseTransactions.get(0).getCurrency());
        Assertions.assertEquals(accountId, databaseTransactions.get(0).getAccountId());

        List<BalanceEntity> databaseBalance = balanceMapper.findBalancesByAccountId(accountId);
        BigDecimal accountMoneyAfter = BigDecimal.valueOf(12 - 5.43 - 1.09);

        Assertions.assertEquals(accountMoneyAfter, databaseBalance.get(0).getAmount());
    }

    @Test
    void testGetTransactions() throws Exception {
        UUID accountId = UUID.fromString("10f73d86-21d7-4116-85de-33d9583c2d23");
        UUID transactionId1 = UUID.randomUUID();
        UUID transactionId2 = UUID.randomUUID();

        TransactionEntity transaction1 = new TransactionEntity(transactionId1,
            accountId, BigDecimal.ONE, BigDecimal.TWO, BalanceCurrency.USD,
                TransactionDirection.IN, "i ran out of ideas for descriptions");
        TransactionEntity transaction2 = new TransactionEntity(transactionId2,
                accountId, BigDecimal.ONE, BigDecimal.TWO, BalanceCurrency.USD,
                TransactionDirection.IN, "i ran out of ideas for descriptions");

        transactionMapper.insertTransaction(transaction1);
        transactionMapper.insertTransaction(transaction2);

        mockMvc.perform(get("/api/transactions/10f73d86-21d7-4116-85de-33d9583c2d23")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.transactions[0].transactionId").value(transactionId1.toString()))
                .andExpect(jsonPath("$.transactions[1].transactionId").value(transactionId2.toString()));
    }
}
