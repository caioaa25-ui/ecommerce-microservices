package com.ecommerce.order.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "orders.exchange";
    public static final String RESERVE_QUEUE = "stock.reserve.queue";
    public static final String RESULT_QUEUE = "order.result.queue";
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String STOCK_RESULT_KEY = "stock.result";

    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue reserveQueue() {
        return new Queue(RESERVE_QUEUE, true);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(RESULT_QUEUE, true);
    }

    @Bean
    public Binding reserveBinding() {
        return BindingBuilder.bind(reserveQueue()).to(ordersExchange()).with(ORDER_CREATED_KEY);
    }

    @Bean
    public Binding resultBinding() {
        return BindingBuilder.bind(resultQueue()).to(ordersExchange()).with(STOCK_RESULT_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
