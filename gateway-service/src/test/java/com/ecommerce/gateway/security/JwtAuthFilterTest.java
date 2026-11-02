package com.ecommerce.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthFilterTest {

    private static final String SECRET = "chave-de-teste-com-pelo-menos-32-bytes-0123456789";
    private final JwtAuthFilter filter = new JwtAuthFilter(SECRET);

    private String tokenValido(String email) {
        return Jwts.builder()
                .subject(email)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void semTokenRetorna401() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/products").build());
        AtomicBoolean chamou = new AtomicBoolean(false);

        filter.filter(exchange, ex -> { chamou.set(true); return Mono.empty(); }).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(chamou.get()).isFalse();
    }

    @Test
    void tokenInvalidoRetorna401() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/products")
                        .header("Authorization", "Bearer token-lixo").build());
        AtomicBoolean chamou = new AtomicBoolean(false);

        filter.filter(exchange, ex -> { chamou.set(true); return Mono.empty(); }).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(chamou.get()).isFalse();
    }

    @Test
    void tokenValidoRepassaComUserId() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/products")
                        .header("Authorization", "Bearer " + tokenValido("ivan@teste.com")).build());
        AtomicReference<ServerWebExchange> repassado = new AtomicReference<>();

        filter.filter(exchange, ex -> { repassado.set(ex); return Mono.empty(); }).block();

        assertThat(repassado.get()).isNotNull();
        assertThat(repassado.get().getRequest().getHeaders().getFirst("X-User-Id"))
                .isEqualTo("ivan@teste.com");
    }

    @Test
    void authSemTokenPassaLivre() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/auth/login").build());
        AtomicBoolean chamou = new AtomicBoolean(false);

        filter.filter(exchange, ex -> { chamou.set(true); return Mono.empty(); }).block();

        assertThat(chamou.get()).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }
}