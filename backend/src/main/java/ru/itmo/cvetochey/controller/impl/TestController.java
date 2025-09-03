package ru.itmo.cvetochey.controller.impl;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/cvet-ochey/api/v1/test")
public class TestController {

    @GetMapping("/cors")
    public Map<String, String> testCors() {
        return Map.of("message", "CORS is working!", "timestamp", String.valueOf(System.currentTimeMillis()));
    }

    @PostMapping("/cors")
    public Map<String, String> testCorsPost(@RequestBody(required = false) Map<String, Object> body) {
        return Map.of("message", "CORS POST is working!", "received", body != null ? body.toString() : "no body");
    }
}
