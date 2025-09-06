package ru.itmo.cvetochey.config;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CorsDisableFilterTest {

    private CorsDisableFilter corsDisableFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        corsDisableFilter = new CorsDisableFilter();
    }

    @Test
    void doFilter_ShouldAddCorsHeaders() throws IOException, ServletException {
        // Given
        when(request.getMethod()).thenReturn("GET");

        // When
        corsDisableFilter.doFilter(request, response, filterChain);

        // Then
        verify(response).setHeader("Access-Control-Allow-Origin", "*");
        verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        verify(response).setHeader("Access-Control-Allow-Headers", "*");
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
        verify(response).setHeader("Access-Control-Max-Age", "3600");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithOptionsRequest_ShouldReturnOkWithoutContinuingChain() throws IOException, ServletException {
        // Given
        when(request.getMethod()).thenReturn("OPTIONS");

        // When
        corsDisableFilter.doFilter(request, response, filterChain);

        // Then
        // Verify CORS headers are still added
        verify(response).setHeader("Access-Control-Allow-Origin", "*");
        verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        verify(response).setHeader("Access-Control-Allow-Headers", "*");
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
        verify(response).setHeader("Access-Control-Max-Age", "3600");
        
        // Verify OPTIONS handling
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(filterChain, never()).doFilter(request, response); // Should NOT continue chain
    }

    @Test
    void doFilter_WithOptionsRequestLowercase_ShouldReturnOkWithoutContinuingChain() throws IOException, ServletException {
        // Given
        when(request.getMethod()).thenReturn("options");

        // When
        corsDisableFilter.doFilter(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilter_WithPostRequest_ShouldContinueChain() throws IOException, ServletException {
        // Given
        when(request.getMethod()).thenReturn("POST");

        // When
        corsDisableFilter.doFilter(request, response, filterChain);

        // Then
        verify(response).setHeader("Access-Control-Allow-Origin", "*");
        verify(filterChain).doFilter(request, response); // Should continue chain
        verify(response, never()).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doFilter_WithPutRequest_ShouldContinueChain() throws IOException, ServletException {
        // Given
        when(request.getMethod()).thenReturn("PUT");

        // When
        corsDisableFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithDeleteRequest_ShouldContinueChain() throws IOException, ServletException {
        // Given
        when(request.getMethod()).thenReturn("DELETE");

        // When
        corsDisableFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_WithNullMethod_ShouldContinueChain() throws IOException, ServletException {
        // Given
        when(request.getMethod()).thenReturn(null);

        // When
        corsDisableFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_OK);
    }
}

