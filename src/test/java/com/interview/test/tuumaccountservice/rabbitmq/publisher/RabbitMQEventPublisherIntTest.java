package com.interview.test.tuumaccountservice.rabbitmq.publisher;

import com.interview.test.tuumaccountservice.rabbitmq.events.AccountCreatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.events.BalanceCreatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.events.BalanceUpdatedEvent;
import com.interview.test.tuumaccountservice.rabbitmq.events.TransactionCreatedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@Testcontainers
@SpringBootTest
public class RabbitMQEventPublisherIntTest {

    @Container
    private static final RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:4.0")
            .withExposedPorts(5672, 15672);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry props) {
        props.add("spring.datasource.url", postgres::getJdbcUrl);
        props.add("spring.datasource.username", postgres::getUsername);
        props.add("spring.datasource.password", postgres::getPassword);

        props.add("spring.rabbitmq.host", rabbitMQ::getHost);
        props.add("spring.rabbitmq.port", () -> rabbitMQ.getMappedPort(5672));
    }

    @Autowired
    private RabbitMQEventPublisher eventPublisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    void testAccountCreatedConsumer() {
        String accountId = UUID.randomUUID().toString();
        String customerId = UUID.randomUUID().toString();
        AccountCreatedEvent event = new AccountCreatedEvent(accountId, customerId, "Estonia");

        eventPublisher.publishAccountCreatedEvent(event);

        AccountCreatedEvent receivedEvent = (AccountCreatedEvent) rabbitTemplate.receiveAndConvert("AccountEventQueue");

        Assertions.assertNotNull(receivedEvent);
        Assertions.assertEquals(event, receivedEvent);
    }

    @Test
    void testBalanceCreatedConsumer() {
        String accountId = UUID.randomUUID().toString();
        String balanceId = UUID.randomUUID().toString();
        BalanceCreatedEvent event = new BalanceCreatedEvent(balanceId, accountId, "USD");

        eventPublisher.publishBalanceCreatedEvent(event);

        BalanceCreatedEvent receivedEvent = (BalanceCreatedEvent) rabbitTemplate.receiveAndConvert("BalanceEventQueue");

        Assertions.assertNotNull(receivedEvent);
        Assertions.assertEquals(event, receivedEvent);
    }

    @Test
    void testBalanceUpdatedConsumer() {
        String balanceId = UUID.randomUUID().toString();
        BalanceUpdatedEvent event = new BalanceUpdatedEvent(balanceId, "12.00");

        eventPublisher.publishBalanceUpdatedEvent(event);

        BalanceUpdatedEvent receivedEvent = (BalanceUpdatedEvent) rabbitTemplate.receiveAndConvert("BalanceEventQueue");

        Assertions.assertNotNull(receivedEvent);
        Assertions.assertEquals(event, receivedEvent);
    }

    @Test
    void testTransactionCreatedConsumer() {
        String transactionId = UUID.randomUUID().toString();
        String accountId = UUID.randomUUID().toString();
        String amount = "12.00";
        String amountAfter = "20.00";
        String currency = "USD";
        String direction = "IN";
        String description = "i am enjoying this task";
        TransactionCreatedEvent event = new TransactionCreatedEvent(transactionId, accountId,
            amount, amountAfter, currency, direction, description);

        eventPublisher.publishTransactionCreatedEvent(event);

        TransactionCreatedEvent receivedEvent = (TransactionCreatedEvent) rabbitTemplate.receiveAndConvert("TransactionEventQueue");

        Assertions.assertNotNull(receivedEvent);
        Assertions.assertEquals(event, receivedEvent);
    }
}
