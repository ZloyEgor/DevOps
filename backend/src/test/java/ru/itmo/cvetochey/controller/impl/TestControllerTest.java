package ru.itmo.cvetochey.controller.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestControllerTest {

    private TestController testController;

    @BeforeEach
    void setUp() {
        testController = new TestController();
    }

    @Test
    void testCors_ShouldReturnSuccessMessage() {
        Map<String, String> response = testController.testCors();
        
        assertNotNull(response);
        assertEquals("CORS is working!", response.get("message"));
        assertNotNull(response.get("timestamp"));
    }

    @Test
    void testCorsPost_ShouldReturnSuccessMessage_WithBody() {
        Map<String, Object> requestBody = Map.of("test", "data");
        
        Map<String, String> response = testController.testCorsPost(requestBody);
        
        assertNotNull(response);
        assertEquals("CORS POST is working!", response.get("message"));
        assertEquals(requestBody.toString(), response.get("received"));
    }

    @Test
    void testCorsPost_ShouldReturnSuccessMessage_WithoutBody() {
        Map<String, String> response = testController.testCorsPost(null);
        
        assertNotNull(response);
        assertEquals("CORS POST is working!", response.get("message"));
        assertEquals("no body", response.get("received"));
    }

    @Test
    void testCorsPost_ShouldReturnSuccessMessage_WithEmptyBody() {
        Map<String, Object> emptyBody = Map.of();
        
        Map<String, String> response = testController.testCorsPost(emptyBody);
        
        assertNotNull(response);
        assertEquals("CORS POST is working!", response.get("message"));
        assertEquals("{}", response.get("received"));
    }
}