package com.ecommerce.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ecommerce.auth.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthService service = new AuthService(userRepository, passwordEncoder, jwtService);

    @Test
    void returnsTokenWhenCredentialsAreValid() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("ivan@teste.com");
        when(user.getPassword()).thenReturn("hash");
        when(userRepository.findByEmail("ivan@teste.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);
        when(jwtService.generateToken("ivan@teste.com")).thenReturn("token-fake");

        LoginResponse response = service.login(new LoginRequest("ivan@teste.com", "senha123"));

        assertThat(response.token()).isEqualTo("token-fake");
    }

    @Test
    void throwsUnauthorizedWhenPasswordIsWrong() {
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("hash");
        when(userRepository.findByEmail("ivan@teste.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", "hash")).thenReturn(false);

        ResponseStatusException ex = catchThrowableOfType(() -> service.login(new LoginRequest("ivan@teste.com", "errada")), ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void throwsUnauthorizedWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = catchThrowableOfType(() -> service.login(new LoginRequest("naoexiste@teste.com", "qualquer")), ResponseStatusException.class);

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}