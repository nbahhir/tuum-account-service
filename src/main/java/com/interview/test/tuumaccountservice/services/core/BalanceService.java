package com.interview.test.tuumaccountservice.services.core;

import com.interview.test.tuumaccountservice.converter.EntityToDtoConverter;
import com.interview.test.tuumaccountservice.dto.Balance;
import com.interview.test.tuumaccountservice.entities.BalanceEntity;
import com.interview.test.tuumaccountservice.enums.BalanceCurrency;
import com.interview.test.tuumaccountservice.mybatis.BalanceMapper;
import com.interview.test.tuumaccountservice.rabbitmq.events.BalanceCreatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.events.BalanceUpdatedEvent;
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
public class BalanceService {

    private final SqlSessionFactory sqlSessionFactory;
    private final RabbitMQEventPublisher publisher;
    private final EntityToDtoConverter converter;

    public List<Balance> findBalances(UUID accountId) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            BalanceMapper mapper = sqlSession.getMapper(BalanceMapper.class);
            return mapper.findBalancesByAccountId(accountId).stream()
                .map(converter::convertBalance)
                .toList();
        }
    }

    public void createBalance(UUID accountId, BalanceCurrency currency) {
        BalanceEntity balanceEntity = new BalanceEntity();
        balanceEntity.setBalanceId(UUID.randomUUID());
        balanceEntity.setAccountId(accountId);
        balanceEntity.setCurrency(currency);
        balanceEntity.setAmount(BigDecimal.ZERO);

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            BalanceMapper mapper = sqlSession.getMapper(BalanceMapper.class);
            mapper.insertBalance(balanceEntity);
        }
        publisher.publishBalanceCreatedEvent(BalanceCreatedEvent.of(balanceEntity));
    }

    public void updateBalance(UUID balanceId, BigDecimal amount) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            BalanceMapper mapper = sqlSession.getMapper(BalanceMapper.class);
            mapper.updateBalance(balanceId, amount);
        }
        publisher.publishBalanceUpdatedEvent(BalanceUpdatedEvent.of(balanceId, amount));
    }
}
