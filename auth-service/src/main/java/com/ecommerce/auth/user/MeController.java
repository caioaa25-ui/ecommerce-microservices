package com.ecommerce.auth.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    @GetMapping("/me")
    public String me(Authentication authentication) {
        return "Logado como: " + authentication.getName();
    }
}