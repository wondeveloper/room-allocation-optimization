package com.smarthost.allocationservice.config.filters;

public class RequestContext {

    public static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    public static final ScopedValue<Long> START_TIME = ScopedValue.newInstance();

    private RequestContext() {
        // Utility class
    }

    // Helper method to calculate request duration
    public static long getRequestDuration() {
        if (START_TIME.isBound()) {
            return System.currentTimeMillis() - START_TIME.get();
        }
        return -1; // Not available
    }

    // Helper method to check if request is taking too long
    public static boolean isSlowRequest(long thresholdMillis) {
        if (START_TIME.isBound()) {
            return (System.currentTimeMillis() - START_TIME.get()) > thresholdMillis;
        }
        return false;
    }
}
