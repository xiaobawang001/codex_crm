package com.example.crm.shared.auth;

import java.util.Set;

/**
 * 当前登录用户上下文（由鉴权拦截器写入，请求结束后清理）。
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> REAL_NAME = new ThreadLocal<>();
    private static final ThreadLocal<Set<String>> ROLES = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId, String username, String realName, Set<String> roles) {
        USER_ID.set(userId);
        USERNAME.set(username);
        REAL_NAME.set(realName);
        ROLES.set(roles);
    }

    public static Long userId() {
        return USER_ID.get();
    }

    public static String username() {
        return USERNAME.get();
    }

    public static String realName() {
        return REAL_NAME.get();
    }

    public static Set<String> roles() {
        return ROLES.get();
    }

    public static boolean hasRole(String role) {
        Set<String> roles = ROLES.get();
        return roles != null && roles.contains(role);
    }

    public static boolean isAdminOrManager() {
        return hasRole("ADMIN") || hasRole("MANAGER");
    }

    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        REAL_NAME.remove();
        ROLES.remove();
    }
}
