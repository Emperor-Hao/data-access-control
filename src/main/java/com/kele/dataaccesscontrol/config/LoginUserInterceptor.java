package com.kele.dataaccesscontrol.config;

import com.kele.dataaccesscontrol.common.LoginUser;
import com.kele.dataaccesscontrol.common.LoginUserHolder;
import com.kele.dataaccesscontrol.component.LoginUserLoader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 演示用登录拦截器：
 * 从请求头 X-User-Id 或请求参数 userId 指定"当前登录用户"，默认 admin(1)。
 */
@Slf4j
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static final String DEFAULT_USER_ID = "1";

    private final LoginUserLoader loginUserLoader;

    public LoginUserInterceptor(LoginUserLoader loginUserLoader) {
        this.loginUserLoader = loginUserLoader;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isBlank()) {
            userIdStr = request.getParameter("userId");
        }
        if (userIdStr == null || userIdStr.isBlank()) {
            userIdStr = DEFAULT_USER_ID;
        }

        LoginUser loginUser = loginUserLoader.load(Long.parseLong(userIdStr));
        if (loginUser != null) {
            LoginUserHolder.set(loginUser);
            log.info("当前登录用户: {} ({})", loginUser.getNickName(), loginUser.getUserId());
        } else {
            log.warn("用户 {} 不存在，本次请求不做数据权限过滤", userIdStr);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LoginUserHolder.clear();
    }
}
