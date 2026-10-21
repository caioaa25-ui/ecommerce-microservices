package com.ecommerce.product.catalog;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StockService {

    private final ProductRepository repository;

    public StockService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductResponse adjust(Long id, int delta) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado"));
        int newStock = product.getStockQuantity() + delta;
        if (newStock < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente");
        }
        product.update(product.getName(), product.getDescription(), product.getPrice(), newStock);
        return ProductResponse.from(product);
    }
}
