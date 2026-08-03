package com.fulfillment.filter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, RequestWindow> requestWindows = new ConcurrentHashMap<>();

    private final Integer requestsPerMinute;

    public RateLimitFilter(@Value("${rate-limit.requests-per-minute}") Integer requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        RequestWindow requestWindow = requestWindows.computeIfAbsent(clientIp, key -> new RequestWindow());

        if (!requestWindow.allowRequest(requestsPerMinute)) {
            response.setStatus(429);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static class RequestWindow {

        private Instant windowStartedAt = Instant.now();

        private final AtomicInteger requestCount = new AtomicInteger();

        synchronized boolean allowRequest(Integer requestsPerMinute) {

            if (Instant.now().isAfter(windowStartedAt.plusSeconds(60))) {
                windowStartedAt = Instant.now();
                requestCount.set(0);
            }

            return requestCount.incrementAndGet() <= requestsPerMinute;
        }
    }
}
