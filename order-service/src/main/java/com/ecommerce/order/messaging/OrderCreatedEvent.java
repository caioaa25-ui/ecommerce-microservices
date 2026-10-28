package com.ecommerce.order.messaging;

import java.util.List;

public record OrderCreatedEvent(Long orderId, List<Item> items) {

    public record Item(Long productId, Integer quantity) {
    }
}
