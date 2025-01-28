package com.interview.test.tuumaccountservice.rabbitmq.publisher;

import com.interview.test.tuumaccountservice.rabbitmq.RabbitMQConfiguration;
import com.interview.test.tuumaccountservice.rabbitmq.events.AccountCreatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.events.BalanceCreatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.events.BalanceUpdatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.events.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RabbitMQEventPublisher {

    public static final String ACCOUNT_CREATED_ROUTING = "event.account.created";
    public static final String BALANCE_CREATED_ROUTING = "event.balance.created";
    public static final String BALANCE_UPDATED_ROUTING = "event.balance.updated";
    public static final String TRANSACTION_CREATED_ROUTING = "event.transaction.created";

    private final RabbitTemplate rabbitTemplate;

    public void publishAccountCreatedEvent(AccountCreatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE, ACCOUNT_CREATED_ROUTING, event);
    }

    public void publishBalanceCreatedEvent(BalanceCreatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE, BALANCE_CREATED_ROUTING, event);
    }

    public void publishBalanceUpdatedEvent(BalanceUpdatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE, BALANCE_UPDATED_ROUTING, event);
    }

    public void publishTransactionCreatedEvent(TransactionCreatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE, TRANSACTION_CREATED_ROUTING, event);
    }
}
