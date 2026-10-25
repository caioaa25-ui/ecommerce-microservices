package com.ecommerce.order.sales;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final PurchaseOrderRepository repository;
    private final ProductClient productClient;

    public OrderService(PurchaseOrderRepository repository, ProductClient productClient) {
        this.repository = repository;
        this.productClient = productClient;
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        PurchaseOrder order = new PurchaseOrder();
        List<OrderItemRequest> reserved = new ArrayList<>();
        try {
            for (OrderItemRequest item : request.items()) {
                ProductInfo product = productClient.getProduct(item.productId());
                productClient.adjustStock(item.productId(), -item.quantity());
                reserved.add(item);
                order.addItem(new OrderItem(product.id(), product.name(), product.price(), item.quantity()));
            }
            return OrderResponse.from(repository.save(order));
        } catch (RuntimeException e) {
            for (OrderItemRequest item : reserved) {
                try {
                    productClient.adjustStock(item.productId(), item.quantity());
                } catch (RuntimeException ignored) {
                }
            }
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return repository.findAll().stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        PurchaseOrder order = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido nao encontrado"));
        return OrderResponse.from(order);
    }
}
