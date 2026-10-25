package com.ecommerce.order.sales;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
public class ProductClient {

    private final RestClient client;

    public ProductClient(@Value("${app.product-service.url}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    public ProductInfo getProduct(Long id) {
        try {
            return client.get().uri("/products/{id}", id).retrieve().body(ProductInfo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto " + id + " nao encontrado");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Servico de produtos indisponivel");
        }
    }

    public void adjustStock(Long id, int delta) {
        try {
            client.patch().uri("/products/{id}/stock", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("delta", delta))
                    .retrieve().toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente para o produto " + id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto " + id + " nao encontrado");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Servico de produtos indisponivel");
        }
    }
}
