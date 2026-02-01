package com.smarthost.allocationservice.config.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.ScopedValue;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final Logger logger = LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get or generate request ID
        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                .filter(id -> !id.isEmpty())
                .orElse(UUID.randomUUID().toString());

        // Add to response header
        response.addHeader(REQUEST_ID_HEADER, requestId);

        // Record start time
        long startTime = System.currentTimeMillis();

        // Run the filter chain within ScopedValue context with multiple values
        ScopedValue.where(RequestContext.REQUEST_ID, requestId)
                .where(RequestContext.START_TIME, startTime)
                .run(() -> {
                    MDC.put("requestId", requestId);
                    MDC.put("startTime", String.valueOf(startTime));
                    try {
                        // Log request start
                        logRequestStart(request, requestId, startTime);
                        // Execute the request
                        filterChain.doFilter(request, response);
                    } catch (IOException | ServletException e) {
                        // Log error with context
                        logRequestError(request, requestId, startTime, e);
                        throw new RuntimeException(e);
                    } finally {
                        // Log request completion
                        logRequestCompletion(request, response, requestId, startTime);
                        MDC.clear();
                    }
                });
    }

    private void logRequestError(HttpServletRequest request, String requestId, long startTime, Exception e) {
        long duration = System.currentTimeMillis() - startTime;
        logger.error("Request failed - ID: {}, Method: {}, URI: {}, Duration: {}ms, Error: {}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                duration,
                e.getMessage(),
                e);
    }

    private void logRequestCompletion(HttpServletRequest request, HttpServletResponse response, String requestId, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        String logLevel = duration > 1000 ? "WARN" : "DEBUG";

        if (logger.isDebugEnabled() || (duration > 1000 && logger.isWarnEnabled())) {
            String message = String.format(
                    "Request completed - ID: %s, Method: %s, URI: %s, Status: %d, Duration: %dms, Log: %s",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    logLevel
            );

            if (duration > 1000) {
                logger.warn(message);
            } else {
                logger.debug(message);
            }
        }

        // Add duration to response header
        response.addHeader("X-Request-Duration", String.valueOf(duration));
        logger.warn("is the request slow (>2000 milliseconds)? {}", RequestContext.isSlowRequest(2000));
        logger.warn("total request round trip duration? {} milliseconds", RequestContext.getRequestDuration());
    }

    private void logRequestStart(HttpServletRequest request, String requestId, long startTime) {
        if (logger.isDebugEnabled()) {
            logger.debug("Request started - ID: {}, Method: {}, URI: {}, StartTime: {}",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    Instant.ofEpochMilli(startTime));
        }
    }

    @EventListener
    public void handleRequestHandledEvent(ServletRequestHandledEvent event) {
        if (RequestContext.START_TIME.isBound()) {
            long duration = System.currentTimeMillis() - RequestContext.START_TIME.get();
            // You can push this to metrics (Micrometer, etc.)
            logger.debug("Request metric - URI: {}, Method: {}, Duration: {}ms",
                    event.getRequestUrl(),
                    event.getMethod(),
                    duration);
        }
    }
}
