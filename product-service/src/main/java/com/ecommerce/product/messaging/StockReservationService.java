package com.ecommerce.product.messaging;

import com.ecommerce.product.catalog.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockReservationService {

    private final StockService stockService;

    public StockReservationService(StockService stockService) {
        this.stockService = stockService;
    }

    @Transactional
    public void reserve(List<OrderCreatedEvent.Item> items) {
        for (OrderCreatedEvent.Item item : items) {
            stockService.adjust(item.productId(), -item.quantity());
        }
    }
}
