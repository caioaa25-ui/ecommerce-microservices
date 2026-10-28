package com.ecommerce.order.messaging;

public record StockResultEvent(Long orderId, boolean reserved, String reason) {
}
