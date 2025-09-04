package ru.itmo.cvetochey.controller.impl;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cvet-ochey/api/v1/test")
public class TestController {

  private static final String MESSAGE_KEY = "message";

  @GetMapping("/cors")
  public Map<String, String> testCors() {
    return Map.of(
        MESSAGE_KEY, "CORS is working!", "timestamp", String.valueOf(System.currentTimeMillis()));
  }

  @PostMapping("/cors")
  public Map<String, String> testCorsPost(@RequestBody(required = false) Map<String, Object> body) {
    return Map.of(
        MESSAGE_KEY,
        "CORS POST is working!",
        "received",
        body != null ? body.toString() : "no body");
  }
}
