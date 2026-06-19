package com.example.internshipapp.controller;

import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "Backend is running");
    }

    @GetMapping("/private")
    public Map<String, String> privateEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "Protected endpoint is working",
                "user", jwt.getClaimAsString("preferred_username"));
    }
}
