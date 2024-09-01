package org.wlow.card.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.NonNullApi;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.constant.UserRole;

import java.io.IOException;

/**
 * 用户权限拦截器, 在校验token之后执行
 */
@Slf4j
@NonNullApi
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        UserRole role = CurrentUser.getRole();
        // /shop下的接口只允许商户和管理员访问
        if (uri.startsWith("/api/shop")) {
            if (!(role == UserRole.Shop || role == UserRole.Admin)) {
                log.error("无权限");
                response.getWriter().write(objectMapper.writeValueAsString(Response.failure(403, "无权限")));
                return false;
            }
        }
        return true;
    }
}
