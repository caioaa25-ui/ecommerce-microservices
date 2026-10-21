package com.ecommerce.product.catalog;

import jakarta.validation.constraints.NotNull;

public record StockAdjustRequest(@NotNull Integer delta) {
}
