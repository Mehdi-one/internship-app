package com.example.internshipapp.controller;

import java.security.Principal;
import java.util.Map;

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
    public Map<String, String> privateEndpoint(Principal principal) {
        return Map.of(
                "message", "Protected endpoint is working",
                "user", principal.getName());
    }
}
