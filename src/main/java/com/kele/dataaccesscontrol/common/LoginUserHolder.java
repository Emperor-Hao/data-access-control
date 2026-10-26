package com.kele.dataaccesscontrol.common;

/**
 * 基于 ThreadLocal 的登录用户持有器。
 */
public final class LoginUserHolder {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();
    public static Long getId() { return HOLDER.get() == null ? 1L : HOLDER.get().getUserId(); }

    private LoginUserHolder() {
    }

    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
