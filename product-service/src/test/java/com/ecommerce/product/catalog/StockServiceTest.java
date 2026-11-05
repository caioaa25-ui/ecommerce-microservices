package com.ecommerce.product.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class StockServiceTest {

    private final ProductRepository repository = mock(ProductRepository.class);
    private final StockService service = new StockService(repository);

    @Test
    void addsStockWhenDeltaIsPositive() {
        Product product = new Product("Camiseta", "Algodao", new BigDecimal("50.00"), 10);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.adjust(1L, 5);

        assertThat(product.getStockQuantity()).isEqualTo(15);
    }

    @Test
    void removesStockWhenDeltaIsNegative() {
        Product product = new Product("Camiseta", "Algodao", new BigDecimal("50.00"), 10);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.adjust(1L, -4);

        assertThat(product.getStockQuantity()).isEqualTo(6);
    }

    @Test
    void throwsNotFoundWhenProductDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = catchThrowableOfType(() -> service.adjust(99L, 1), ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void throwsConflictAndKeepsStockWhenStockWouldBeNegative() {
        Product product = new Product("Camiseta", "Algodao", new BigDecimal("50.00"), 3);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        ResponseStatusException ex = catchThrowableOfType(() -> service.adjust(1L, -5), ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(product.getStockQuantity()).isEqualTo(3);
    }
}
