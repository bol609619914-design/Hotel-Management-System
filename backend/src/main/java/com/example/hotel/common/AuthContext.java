package com.example.hotel.common;

public final class AuthContext {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USERNAME = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(Long userId, String username) {
        CURRENT_USER_ID.set(userId);
        CURRENT_USERNAME.set(username);
    }

    public static Long currentUserId() {
        return CURRENT_USER_ID.get();
    }

    public static String currentUsername() {
        return CURRENT_USERNAME.get();
    }

    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USERNAME.remove();
    }
}
