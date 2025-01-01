package com.inception.app.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    
    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
            "status", "ok",
            "version", "0.1.0"
        );
    }
}
