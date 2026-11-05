package com.ecommerce.order.sales;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ecommerce.order.messaging.OrderEventPublisher;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class OrderServiceTest {

    private final PurchaseOrderRepository repository = mock(PurchaseOrderRepository.class);
    private final ProductClient productClient = mock(ProductClient.class);
    private final OrderEventPublisher publisher = mock(OrderEventPublisher.class);
    private final OrderService service = new OrderService(repository, productClient, publisher);

    @Test
    void findByIdThrowsNotFoundWhenOrderDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = catchThrowableOfType(() -> service.findById(99L), ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}