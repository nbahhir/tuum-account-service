package com.interview.test.tuumaccountservice.services.core;

import com.interview.test.tuumaccountservice.converter.EntityToDtoConverter;
import com.interview.test.tuumaccountservice.dto.Account;
import com.interview.test.tuumaccountservice.entities.AccountEntity;
import com.interview.test.tuumaccountservice.mybatis.AccountMapper;
import com.interview.test.tuumaccountservice.rabbitmq.publisher.RabbitMQEventPublisher;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    SqlSessionFactory sqlSessionFactoryMock;
    @Mock
    SqlSession sessionMock;
    @Mock
    AccountMapper accountMapperMock;
    @Mock
    EntityToDtoConverter converterMock;
    @Mock
    RabbitMQEventPublisher publisher;

    @InjectMocks
    AccountService accountService;

    @BeforeEach
    void setup() {
        Mockito.when(sqlSessionFactoryMock.openSession()).thenReturn(sessionMock);
        Mockito.when(sessionMock.getMapper(AccountMapper.class)).thenReturn(accountMapperMock);
    }

    @Test
    void testCreatesAccount() {
        UUID testCustomerUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        String testCountry = "Revachol";

        UUID resultAccountId = accountService.createAccount(testCustomerUUID, testCountry);

        ArgumentCaptor<AccountEntity> accountCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        Mockito.verify(accountMapperMock).insertAccount(accountCaptor.capture());
        AccountEntity captured = accountCaptor.getValue();

        Assertions.assertNotNull(captured);
        Assertions.assertEquals(testCustomerUUID, captured.getCustomerId());
        Assertions.assertEquals(resultAccountId, captured.getAccountId());
        Assertions.assertEquals(testCountry, captured.getCountry());
    }

    @Test
    void testFindsByAccountIdWhenExists() {
        UUID testCustomerUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        UUID testAccountUUID = UUID.fromString("da84e557-7cf8-4e92-9bd7-34a59e462ba1");
        String testCountry = "Revachol";
        AccountEntity testEntity = new AccountEntity();
        testEntity.setAccountId(testAccountUUID);
        testEntity.setCustomerId(testCustomerUUID);
        testEntity.setCountry(testCountry);
        Account testAccount = new Account(testCustomerUUID, testAccountUUID, testCountry);

        Mockito.when(accountMapperMock.findAccountByAccountId(testAccountUUID)).thenReturn(testEntity);
        Mockito.when(converterMock.convertAccount(testEntity)).thenReturn(testAccount);

        Account resultAccount = accountService.findAccountByAccountId(testAccountUUID).get();

        ArgumentCaptor<AccountEntity> accountCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        Mockito.verify(converterMock).convertAccount(accountCaptor.capture());
        AccountEntity capturedEntity = accountCaptor.getValue();

        Assertions.assertEquals(testEntity, capturedEntity);
        Assertions.assertEquals(testAccount, resultAccount);
    }

    @Test
    void testFindsByCustomerIdWhenExists() {
        UUID testCustomerUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        UUID testAccountUUID = UUID.fromString("da84e557-7cf8-4e92-9bd7-34a59e462ba1");
        String testCountry = "Revachol";
        AccountEntity testEntity = new AccountEntity();
        testEntity.setAccountId(testAccountUUID);
        testEntity.setCustomerId(testCustomerUUID);
        testEntity.setCountry(testCountry);
        Account testAccount = new Account(testCustomerUUID, testAccountUUID, testCountry);

        Mockito.when(accountMapperMock.findAccountByCustomerId(testCustomerUUID)).thenReturn(testEntity);
        Mockito.when(converterMock.convertAccount(testEntity)).thenReturn(testAccount);

        Account resultAccount = accountService.findAccountByCustomerId(testCustomerUUID).get();

        ArgumentCaptor<AccountEntity> accountCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        Mockito.verify(converterMock).convertAccount(accountCaptor.capture());
        AccountEntity capturedEntity = accountCaptor.getValue();

        Assertions.assertEquals(testEntity, capturedEntity);
        Assertions.assertEquals(testAccount, resultAccount);
    }

    @Test
    void testDoesntFindByCustomerIdWhenNotExists() {
        UUID testCustomerUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        UUID testAccountUUID = UUID.fromString("da84e557-7cf8-4e92-9bd7-34a59e462ba1");
        String testCountry = "Revachol";
        AccountEntity testEntity = new AccountEntity();
        testEntity.setAccountId(testAccountUUID);
        testEntity.setCustomerId(testCustomerUUID);
        testEntity.setCountry(testCountry);

        Mockito.when(accountMapperMock.findAccountByCustomerId(testCustomerUUID)).thenReturn(null);

        Optional<Account> resultAccount = accountService.findAccountByCustomerId(testCustomerUUID);

        Mockito.verify(converterMock, Mockito.never()).convertAccount(Mockito.any());

        Assertions.assertTrue(resultAccount.isEmpty());
    }
}
