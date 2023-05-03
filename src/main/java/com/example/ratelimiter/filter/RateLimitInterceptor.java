package com.example.ratelimiter.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Value("${rate.limit}")
    private int RATE_LIMIT;

    private final AtomicInteger requestCounter = new AtomicInteger(0);
    private long lastResetTime = System.currentTimeMillis();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        resetCounterIfNecessary();

        if (exceedsRateLimit()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(getRateLimitExceededResponse());
            return false;
        }

        incrementRequestCount();
        return true;
    }

    private boolean exceedsRateLimit() {
        return requestCounter.get() >= RATE_LIMIT;
    }

    private void incrementRequestCount() {
        requestCounter.incrementAndGet();
    }

    private void resetCounterIfNecessary() {
        long currentTime = System.currentTimeMillis();
        long timeSinceReset = currentTime - lastResetTime;

        if (timeSinceReset >= TimeUnit.MINUTES.toMillis(1)) {
            requestCounter.set(0);
            lastResetTime = currentTime;
        }
    }

    private String getRateLimitExceededResponse() throws IOException {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", "Too many requests. Please try again later.");
        return objectMapper.writeValueAsString(responseBody);
    }
}
