package com.interview.test.tuumaccountservice.services.core;

import com.interview.test.tuumaccountservice.converter.EntityToDtoConverter;
import com.interview.test.tuumaccountservice.dto.Account;
import com.interview.test.tuumaccountservice.entities.AccountEntity;
import com.interview.test.tuumaccountservice.mybatis.AccountMapper;
import com.interview.test.tuumaccountservice.rabbitmq.events.AccountCreatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.publisher.RabbitMQEventPublisher;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final SqlSessionFactory sqlSessionFactory;
    private final RabbitMQEventPublisher publisher;
    private final EntityToDtoConverter converter;

    public UUID createAccount(UUID customerId, String country) {
        AccountEntity accountEntity = new AccountEntity();
        UUID accountId = UUID.randomUUID();
        accountEntity.setAccountId(accountId);
        accountEntity.setCountry(country);
        accountEntity.setCustomerId(customerId);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            accountMapper.insertAccount(accountEntity);
        }
        publisher.publishAccountCreatedEvent(AccountCreatedEvent.of(accountEntity));
        return accountId;
    }

    public Optional<Account> findAccountByAccountId(UUID accountId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            return Optional.ofNullable(accountMapper.findAccountByAccountId(accountId))
                    .map(converter::convertAccount);
        }
    }

    public Optional<Account> findAccountByCustomerId(UUID customerId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            return Optional.ofNullable(accountMapper.findAccountByCustomerId(customerId))
                    .map(converter::convertAccount);
        }
    }
}
