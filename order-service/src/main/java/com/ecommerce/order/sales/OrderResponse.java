package com.ecommerce.order.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, OrderStatus status, BigDecimal total, LocalDateTime createdAt, List<OrderItemResponse> items) {

    public static OrderResponse from(PurchaseOrder o) {
        return new OrderResponse(o.getId(), o.getStatus(), o.getTotal(), o.getCreatedAt(),
                o.getItems().stream().map(OrderItemResponse::from).toList());
    }
}
