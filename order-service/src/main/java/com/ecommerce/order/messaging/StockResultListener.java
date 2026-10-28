package com.ecommerce.order.messaging;

import com.ecommerce.order.sales.OrderStatus;
import com.ecommerce.order.sales.PurchaseOrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StockResultListener {

    private final PurchaseOrderRepository repository;

    public StockResultListener(PurchaseOrderRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.RESULT_QUEUE)
    @Transactional
    public void onStockResult(StockResultEvent event) {
        repository.findById(event.orderId()).ifPresent(order -> {
            if (order.getStatus() == OrderStatus.PENDING) {
                if (event.reserved()) {
                    order.confirm();
                } else {
                    order.cancel();
                }
            }
        });
    }
}
