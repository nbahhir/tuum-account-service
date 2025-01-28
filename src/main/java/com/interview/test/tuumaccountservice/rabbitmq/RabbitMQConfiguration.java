package com.interview.test.tuumaccountservice.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Choosing the topic exchange approach, as this allows the consumer to be more flexible
// in which events they consume.

@Configuration
public class RabbitMQConfiguration {
    public static final String EXCHANGE = "events.exchange";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue accountQueue() {
        return new Queue("AccountEventQueue");
    }

    @Bean
    public Queue balanceQueue() {
        return new Queue("BalanceEventQueue");
    }

    @Bean
    public Queue transactionQueue() {
        return new Queue("TransactionEventQueue");
    }

    @Bean
    public Queue allEventsQueue() {
        return new Queue("AllEventsQueue", true);
    }

    @Bean
    public Binding bindAccountQueue(TopicExchange topicExchange, Queue accountQueue) {
        return BindingBuilder.bind(accountQueue).to(topicExchange).with("event.account.*");
    }

    @Bean
    public Binding bindBalanceQueue(TopicExchange topicExchange, Queue balanceQueue) {
        return BindingBuilder.bind(balanceQueue).to(topicExchange).with("event.balance.*");
    }

    @Bean
    public Binding bindTransactionQueue(TopicExchange topicExchange, Queue transactionQueue) {
        return BindingBuilder.bind(transactionQueue).to(topicExchange).with("event.transaction.*");
    }

    @Bean
    public Binding bindAllEventsQueue(TopicExchange topicExchange, Queue allEventsQueue) {
        return BindingBuilder.bind(allEventsQueue).to(topicExchange).with("event.#");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
