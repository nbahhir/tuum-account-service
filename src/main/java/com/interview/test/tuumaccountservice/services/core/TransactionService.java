package com.interview.test.tuumaccountservice.services.core;

import com.interview.test.tuumaccountservice.converter.EntityToDtoConverter;
import com.interview.test.tuumaccountservice.dto.Transaction;
import com.interview.test.tuumaccountservice.entities.TransactionEntity;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.enums.TransactionDirection;
import com.interview.test.tuumaccountservice.mybatis.TransactionMapper;
import com.interview.test.tuumaccountservice.rabbitmq.events.TransactionCreatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.publisher.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final SqlSessionFactory sqlSessionFactory;
    private final RabbitMQEventPublisher publisher;
    private final EntityToDtoConverter converter;

    public List<Transaction> findTransactions(UUID accountId) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            TransactionMapper mapper = sqlSession.getMapper(TransactionMapper.class);
            return mapper.findTransactionsByAccountId(accountId).stream()
                    .map(converter::convertTransaction)
                    .toList();
        }
    }

    public UUID createTransaction(UUID accountId, BigDecimal amount, BigDecimal amountAfter,
        BalanceCurrency currency, TransactionDirection direction, String description) {

        TransactionEntity transactionEntity = new TransactionEntity();
        UUID transactionId = UUID.randomUUID();
        transactionEntity.setTransactionId(transactionId);
        transactionEntity.setAccountId(accountId);
        transactionEntity.setAmount(amount);
        transactionEntity.setBalanceAfterTransaction(amountAfter);
        transactionEntity.setCurrency(currency);
        transactionEntity.setDirection(direction);
        transactionEntity.setDescription(description);

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            TransactionMapper mapper = sqlSession.getMapper(TransactionMapper.class);
            mapper.insertTransaction(transactionEntity);
        }
        publisher.publishTransactionCreatedEvent(TransactionCreatedEvent.of(transactionEntity));
        return transactionId;
    }
}
