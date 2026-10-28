package com.ecommerce.order.messaging;

import com.ecommerce.order.sales.PurchaseOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void orderCreated(PurchaseOrder order) {
        List<OrderCreatedEvent.Item> items = order.getItems().stream()
                .map(i -> new OrderCreatedEvent.Item(i.getProductId(), i.getQuantity()))
                .toList();
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ORDER_CREATED_KEY,
                new OrderCreatedEvent(order.getId(), items));
    }
}
