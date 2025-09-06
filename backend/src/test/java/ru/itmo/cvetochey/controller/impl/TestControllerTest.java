package ru.itmo.cvetochey.controller.impl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TestController.class)
@ActiveProfiles("test")
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCors_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(get("/cvet-ochey/api/v1/test/cors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("CORS is working!"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCorsPost_ShouldReturnSuccessMessage_WithBody() throws Exception {
        Map<String, Object> requestBody = Map.of("test", "data");

        mockMvc.perform(post("/cvet-ochey/api/v1/test/cors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("CORS POST is working!"))
            .andExpect(jsonPath("$.received").value(requestBody.toString()));
    }

    @Test
    void testCorsPost_ShouldReturnSuccessMessage_WithoutBody() throws Exception {
        mockMvc.perform(post("/cvet-ochey/api/v1/test/cors")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("CORS POST is working!"))
            .andExpect(jsonPath("$.received").value("no body"));
    }

    @Test
    void testCorsPost_ShouldReturnSuccessMessage_WithEmptyBody() throws Exception {
        mockMvc.perform(post("/cvet-ochey/api/v1/test/cors")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("CORS POST is working!"))
            .andExpect(jsonPath("$.received").value("{}"));
    }
}
