package com.interview.test.tuumaccountservice.mybatis;

import com.interview.test.tuumaccountservice.entities.AccountEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;


@SpringBootTest
@Testcontainers
class AccountMapperIntTest {

    @Autowired
    AccountMapper accountMapper;
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry props) {
        props.add("spring.datasource.url", postgres::getJdbcUrl);
        props.add("spring.datasource.username", postgres::getUsername);
        props.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testInsertionAndFindWorks() {
        UUID testAccountId = UUID.randomUUID();
        String testCountry = "Neverland";
        UUID testCustomerId = UUID.randomUUID();
        var accountCreated = new AccountEntity();
        accountCreated.setAccountId(testAccountId);
        accountCreated.setCountry(testCountry);
        accountCreated.setCustomerId(testCustomerId);
        accountMapper.insertAccount(accountCreated);

        // Would personally prefer to user assertJ here, but the document asks for JUnit.
        AccountEntity foundAccount = accountMapper.findAccountByAccountId(testAccountId);
        AccountEntity shouldBeEmptyAccount = accountMapper.findAccountByAccountId(UUID.randomUUID());
        AccountEntity foundAccountByCustomerId = accountMapper.findAccountByCustomerId(testCustomerId);
        AccountEntity shouldBeEmptyAccountByCustomerId = accountMapper.findAccountByCustomerId(UUID.randomUUID());

        Assertions.assertNull(shouldBeEmptyAccount);
        Assertions.assertNull(shouldBeEmptyAccountByCustomerId);
        Assertions.assertNotNull(foundAccount);
        Assertions.assertNotNull(foundAccountByCustomerId);

        Assertions.assertEquals(testCountry, foundAccount.getCountry());
        Assertions.assertEquals(testCountry, foundAccountByCustomerId.getCountry());
        Assertions.assertEquals(testCustomerId, foundAccount.getCustomerId());
        Assertions.assertEquals(testCustomerId, foundAccountByCustomerId.getCustomerId());
        Assertions.assertEquals(testAccountId, foundAccount.getAccountId());
        Assertions.assertEquals(testAccountId, foundAccountByCustomerId.getAccountId());
    }

    @Test
    void testDoesntWorkWhenAlreadyAccountExists() {
        UUID testAccountId = UUID.randomUUID();
        String testCountry = "Neverland";
        UUID testCustomerId = UUID.randomUUID();
        var accountCreated = new AccountEntity();
        accountCreated.setAccountId(testAccountId);
        accountCreated.setCountry(testCountry);
        accountCreated.setCustomerId(testCustomerId);
        accountMapper.insertAccount(accountCreated);

        accountCreated.setAccountId(UUID.randomUUID());
        Assertions.assertThrows(Exception.class, () -> accountMapper.insertAccount(accountCreated));
    }
}
