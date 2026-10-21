package com.ecommerce.product.catalog;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class StockController {

    private final StockService service;

    public StockController(StockService service) {
        this.service = service;
    }

    @PatchMapping("/{id}/stock")
    public ProductResponse adjust(@PathVariable Long id, @Valid @RequestBody StockAdjustRequest request) {
        return service.adjust(id, request.delta());
    }
}
