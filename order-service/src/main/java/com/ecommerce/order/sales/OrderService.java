package com.ecommerce.order.sales;

import com.ecommerce.order.messaging.OrderEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrderService {

    private final PurchaseOrderRepository repository;
    private final ProductClient productClient;
    private final OrderEventPublisher publisher;

    public OrderService(PurchaseOrderRepository repository, ProductClient productClient, OrderEventPublisher publisher) {
        this.repository = repository;
        this.productClient = productClient;
        this.publisher = publisher;
    }

    public OrderResponse create(OrderRequest request) {
        PurchaseOrder order = new PurchaseOrder();
        for (OrderItemRequest item : request.items()) {
            ProductInfo product = productClient.getProduct(item.productId());
            order.addItem(new OrderItem(product.id(), product.name(), product.price(), item.quantity()));
        }
        PurchaseOrder saved = repository.save(order);
        publisher.orderCreated(saved);
        return OrderResponse.from(saved);
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
