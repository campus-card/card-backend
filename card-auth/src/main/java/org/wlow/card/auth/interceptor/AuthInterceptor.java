package org.wlow.card.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.micrometer.common.lang.NonNullApi;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.auth.JWTUtil;
import org.wlow.card.auth.TokenType;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.constant.UserRole;

import java.io.IOException;

@Slf4j
@NonNullApi
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader("Authorization");

        if (token == null || token.isBlank()) {
            log.warn("未携带token或token为空");
            response.getWriter().write(objectMapper.writeValueAsString(Response.failure(400, "未携带token或token为空")));
            return false;
        }
        Claims payload;
        try {
            payload = jwtUtil.parseToken(token, TokenType.ACCESS);
        } catch (SignatureException e) {
            log.warn("token解析失败: {}", e.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(Response.failure(401, "无效的token")));
            return false;
        } catch (ExpiredJwtException e) {
            log.warn("token已过期: {}", e.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(Response.failure(401, "token已过期")));
            return false;
        }
        int userId = payload.get("id", Integer.class);
        String username = payload.get("username", String.class);
        UserRole role = UserRole.valueOf(payload.get("role", String.class));
        // 存储当前用户信息
        CurrentUser.setAll(userId, username, role);
        return true;
    }
}
