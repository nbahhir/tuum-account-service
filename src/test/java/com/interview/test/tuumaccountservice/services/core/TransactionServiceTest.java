package com.interview.test.tuumaccountservice.services.core;

import com.interview.test.tuumaccountservice.converter.EntityToDtoConverter;
import com.interview.test.tuumaccountservice.dto.Transaction;
import com.interview.test.tuumaccountservice.entities.TransactionEntity;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;
import com.interview.test.tuumaccountservice.mybatis.TransactionMapper;
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
class TransactionServiceTest {
    @Mock
    SqlSessionFactory sqlSessionFactoryMock;
    @Mock
    SqlSession sessionMock;
    @Mock
    TransactionMapper transactionMapperMock;
    @Mock
    EntityToDtoConverter converterMock;

    @InjectMocks
    TransactionService transactionService;

    @BeforeEach
    void setup() {
        Mockito.when(sqlSessionFactoryMock.openSession()).thenReturn(sessionMock);
        Mockito.when(sessionMock.getMapper(TransactionMapper.class)).thenReturn(transactionMapperMock);
    }

    @Test
    void testFindsTransactions() {
        UUID testAccountUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        TransactionEntity transaction1 = new TransactionEntity(UUID.randomUUID(), testAccountUUID,
                BigDecimal.ONE, BigDecimal.TWO, BalanceCurrency.USD, TransactionDirection.IN, "descr");
        TransactionEntity transaction2 = new TransactionEntity(UUID.randomUUID(), testAccountUUID,
                BigDecimal.ONE, BigDecimal.TEN, BalanceCurrency.EUR, TransactionDirection.OUT, "descr2");

        Transaction transactionDto1 = new Transaction(transaction1.getTransactionId(), testAccountUUID,
            transaction1.getAmount(), transaction1.getBalanceAfterTransaction(), transaction1.getCurrency(),
            transaction1.getDirection(), transaction1.getDescription());
        Transaction transactionDto2 = new Transaction(transaction2.getTransactionId(), testAccountUUID,
            transaction2.getAmount(), transaction2.getBalanceAfterTransaction(), transaction2.getCurrency(),
            transaction2.getDirection(), transaction2.getDescription());

        List<TransactionEntity> testEntities = List.of(transaction1, transaction2);

        Mockito.when(transactionMapperMock.findTransactionsByAccountId(testAccountUUID)).thenReturn(testEntities);
        Mockito.when(converterMock.convertTransaction(transaction1)).thenReturn(transactionDto1);
        Mockito.when(converterMock.convertTransaction(transaction2)).thenReturn(transactionDto2);

        List<Transaction> resultTransactions = transactionService.findTransactions(testAccountUUID);

        Assertions.assertEquals(2, resultTransactions.size());
        Assertions.assertTrue(resultTransactions.contains(transactionDto1));
        Assertions.assertTrue(resultTransactions.contains(transactionDto2));
    }

    @Test
    void testCreatesBalances() {
        UUID testAccountUUID = UUID.fromString("38464f86-b734-4d11-be6d-30aea9e98312");
        BigDecimal amount = BigDecimal.ONE;
        BigDecimal amountAfter = BigDecimal.TEN;
        BalanceCurrency balanceCurrency = BalanceCurrency.EUR;
        TransactionDirection direction = TransactionDirection.IN;
        String description = "glory to kier";

        transactionService.createTransaction(testAccountUUID, amount, amountAfter,
            balanceCurrency, direction, description);

        ArgumentCaptor<TransactionEntity> entityCaptor = ArgumentCaptor.forClass(TransactionEntity.class);
        Mockito.verify(transactionMapperMock).insertTransaction(entityCaptor.capture());
        TransactionEntity capturedEntity = entityCaptor.getValue();

        Assertions.assertNotNull(capturedEntity);
        Assertions.assertEquals(balanceCurrency, capturedEntity.getCurrency());
        Assertions.assertEquals(testAccountUUID, capturedEntity.getAccountId());
        Assertions.assertEquals(amount, capturedEntity.getAmount());
        Assertions.assertEquals(amountAfter, capturedEntity.getBalanceAfterTransaction());
        Assertions.assertEquals(direction, capturedEntity.getDirection());
        Assertions.assertEquals(description, capturedEntity.getDescription());
    }
}
