package com.ecommerce.product.messaging;

public record StockResultEvent(Long orderId, boolean reserved, String reason) {
}
