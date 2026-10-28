package com.ecommerce.product.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OrderEventListener {

    private final StockReservationService reservationService;
    private final RabbitTemplate rabbitTemplate;

    public OrderEventListener(StockReservationService reservationService, RabbitTemplate rabbitTemplate) {
        this.reservationService = reservationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.RESERVE_QUEUE)
    public void onOrderCreated(OrderCreatedEvent event) {
        StockResultEvent result;
        try {
            reservationService.reserve(event.items());
            result = new StockResultEvent(event.orderId(), true, null);
        } catch (RuntimeException e) {
            String reason = (e instanceof ResponseStatusException r && r.getReason() != null)
                    ? r.getReason() : "Erro ao reservar estoque";
            result = new StockResultEvent(event.orderId(), false, reason);
        }
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.STOCK_RESULT_KEY, result);
    }
}
