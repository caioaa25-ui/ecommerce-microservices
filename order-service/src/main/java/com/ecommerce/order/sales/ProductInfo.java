package com.ecommerce.order.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductInfo(Long id, String name, BigDecimal price, Integer stockQuantity) {
}
