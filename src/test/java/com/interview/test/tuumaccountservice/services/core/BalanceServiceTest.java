package com.interview.test.tuumaccountservice.services.core;

import com.interview.test.tuumaccountservice.converter.EntityToDtoConverter;
import com.interview.test.tuumaccountservice.dto.Balance;
import com.interview.test.tuumaccountservice.entities.BalanceEntity;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.mybatis.BalanceMapper;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {
    @Mock
    SqlSessionFactory sqlSessionFactoryMock;
    @Mock
    SqlSession sessionMock;
    @Mock
    BalanceMapper balanceMapperMock;
    @Mock
    EntityToDtoConverter converterMock;
    @Mock
    RabbitMQEventPublisher publisher;

    @InjectMocks
    BalanceService balanceService;

    @BeforeEach
    void setup() {
        Mockito.when(sqlSessionFactoryMock.openSession()).thenReturn(sessionMock);
        Mockito.when(sessionMock.getMapper(BalanceMapper.class)).thenReturn(balanceMapperMock);
    }

    @Test
    void testFindsBalances() {
        UUID testAccountUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        BalanceEntity balance1 = new BalanceEntity(UUID.randomUUID(), BalanceCurrency.USD,
            testAccountUUID, BigDecimal.ONE);
        BalanceEntity balance2 = new BalanceEntity(UUID.randomUUID(), BalanceCurrency.SEK,
                testAccountUUID, BigDecimal.ONE);
        Balance balanceDto1 = new Balance(balance1.getBalanceId(), testAccountUUID,
            balance1.getCurrency(), balance1.getAmount());
        Balance balanceDto2 = new Balance(balance2.getBalanceId(), testAccountUUID,
                balance2.getCurrency(), balance2.getAmount());

        List<BalanceEntity> testBalances = List.of(balance1, balance2);

        Mockito.when(balanceMapperMock.findBalancesByAccountId(testAccountUUID)).thenReturn(testBalances);
        Mockito.when(converterMock.convertBalance(balance1)).thenReturn(balanceDto1);
        Mockito.when(converterMock.convertBalance(balance2)).thenReturn(balanceDto2);

        List<Balance> resultBalances = balanceService.findBalances(testAccountUUID);

        Assertions.assertEquals(2, resultBalances.size());
        Assertions.assertTrue(resultBalances.contains(balanceDto1));
        Assertions.assertTrue(resultBalances.contains(balanceDto2));
    }

    @Test
    void testCreatesBalances() {
        UUID testAccountUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        BalanceCurrency testCurrency = BalanceCurrency.EUR;

        balanceService.createBalance(testAccountUUID, testCurrency);

        ArgumentCaptor<BalanceEntity> entityCaptor = ArgumentCaptor.forClass(BalanceEntity.class);
        Mockito.verify(balanceMapperMock).insertBalance(entityCaptor.capture());
        BalanceEntity capturedEntity = entityCaptor.getValue();

        Assertions.assertNotNull(capturedEntity);
        Assertions.assertEquals(testCurrency, capturedEntity.getCurrency());
        Assertions.assertEquals(testAccountUUID, capturedEntity.getAccountId());
        Assertions.assertNotNull(capturedEntity.getBalanceId());
        Assertions.assertNotNull(capturedEntity.getAmount());
    }

    @Test
    void testUpdatesBalances() {
        UUID testBalanceUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        BigDecimal testAmount = BigDecimal.TWO;
        balanceService.updateBalance(testBalanceUUID, testAmount);

        Mockito.verify(balanceMapperMock).updateBalance(testBalanceUUID, testAmount);
    }
}
