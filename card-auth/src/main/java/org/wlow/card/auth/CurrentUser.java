package org.wlow.card.auth;

import org.wlow.card.data.data.constant.UserRole;

/**
 * 当前用户信息
 */
public class CurrentUser {
    private static final ThreadLocal<Integer> ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<UserRole> ROLE = new ThreadLocal<>();

    public static void setId(Integer id) {
        ID.set(id);
    }
    public static void setUsername(String username) {
        USERNAME.set(username);
    }
    public static void setRole(UserRole role) {
        ROLE.set(role);
    }
    public static void setAll(Integer id, String username, UserRole role) {
        ID.set(id);
        USERNAME.set(username);
        ROLE.set(role);
    }
    public static Integer getId() {
        return ID.get();
    }
    public static String getUsername() {
        return USERNAME.get();
    }
    public static UserRole getRole() {
        return ROLE.get();
    }
}
