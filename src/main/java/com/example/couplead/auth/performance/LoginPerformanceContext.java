package com.example.couplead.auth.performance;

public final class LoginPerformanceContext {

    private static final ThreadLocal<Long> USER_QUERY_NANOS = ThreadLocal.withInitial(() -> 0L);

    private static final ThreadLocal<Long> PASSWORD_NANOS = ThreadLocal.withInitial(() -> 0L);

    private LoginPerformanceContext() {
    }

    public static void start() {
        USER_QUERY_NANOS.set(0L);
        PASSWORD_NANOS.set(0L);
    }

    public static void recordUserQuery(long nanos) {
        USER_QUERY_NANOS.set(nanos);
    }

    public static void recordPasswordCheck(long nanos) {
        PASSWORD_NANOS.set(nanos);
    }

    public static long getUserQueryNanos() {
        return USER_QUERY_NANOS.get();
    }

    public static long getPasswordNanos() {
        return PASSWORD_NANOS.get();
    }

    public static void clear() {
        USER_QUERY_NANOS.remove();
        PASSWORD_NANOS.remove();
    }
}