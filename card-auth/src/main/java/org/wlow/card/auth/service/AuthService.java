package org.wlow.card.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.wlow.card.auth.JWTUtil;
import org.wlow.card.auth.TokenType;
import org.wlow.card.data.data.DTO.DTOUser;
import org.wlow.card.data.data.PO.User;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.constant.UserRole;
import org.wlow.card.data.mapper.UserMapper;
import org.wlow.card.data.redis.RedisUtil;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ObjectMapper objectMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 注册 <br>
     * 每种role下的用户名唯一
     */
    public Response<String> signUp(String username, String password, UserRole role) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        query.eq("role", role);
        if (userMapper.exists(query)) {
            return Response.failure(400, "用户名已被占用");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        userMapper.insert(user);
        return Response.ok();
    }

    /**
     * 登录
     */
    public Response<Object> login(String username, String password, UserRole role) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        query.eq("role", role);
        User user = userMapper.selectOne(query);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Response.failure(400, "用户名或密码错误");
        }
        // 生成token, refreshToken存到redis并设置过期时间
        String accessToken = jwtUtil.getToken(user.getId(), user.getUsername(), user.getRole(), TokenType.ACCESS);
        String refreshToken = jwtUtil.getToken(user.getId(), user.getUsername(), user.getRole(), TokenType.REFRESH);
        // RefreshToken:role:userId
        redisUtil.set("RefreshToken:" + user.getId(), refreshToken, jwtUtil.REFRESH_EXPIRATION);
        // 同时缓存用户信息
        try {
            redisUtil.set("UserInfo:" + user.getId(), objectMapper.writeValueAsString(DTOUser.fromPO(user)), jwtUtil.REFRESH_EXPIRATION);
        } catch (JsonProcessingException e) {
            log.error("缓存用户信息失败: {}", e.getMessage());
        }

        return Response.success(Map.of("id", user.getId(), "username", user.getUsername(), "role", role.value, "accessToken", accessToken, "refreshToken", refreshToken));
    }

    /**
     * 刷新token
     */
    public Response<Object> refresh(String refreshToken) {
        Claims payload;
        try {
            payload = jwtUtil.parseToken(refreshToken, TokenType.REFRESH);
        } catch (SignatureException e) {
            log.error("refreshToken解析失败: {}", e.getMessage());
            return Response.failure(400, "无效的refreshToken");
        } catch (ExpiredJwtException e) {
            log.error("refreshToken已过期: {}", e.getMessage());
            return Response.failure(400, "refreshToken已过期");
        }
        Integer userId = payload.get("id", Integer.class);
        String username = payload.get("username", String.class);
        String role = payload.get("role", String.class);

        String refreshTokenCached = redisUtil.get("RefreshToken:" + userId);
        if (!refreshToken.equals(refreshTokenCached)) {
            // refreshToken不匹配
            return Response.forbidden("无效的refreshToken");
        }
        // 新的accessToken
        String accessToken = jwtUtil.getToken(userId, username, UserRole.valueOf(role), TokenType.ACCESS);
        Map<String, String> res = new HashMap<>();
        res.put("accessToken", accessToken);
        Long expiration = redisUtil.getExpire("RefreshToken:" + userId);
        if (expiration <= jwtUtil.REFRESH_BOUND) {
            // 刷新refreshToken
            String newRefreshToken = jwtUtil.getToken(userId, username, UserRole.valueOf(role), TokenType.REFRESH);
            redisUtil.set("RefreshToken:" + userId, newRefreshToken, jwtUtil.REFRESH_EXPIRATION);
            // 更新用户信息缓存
            redisUtil.set("UserInfo:" + userId, redisUtil.get("UserInfo:" + userId), jwtUtil.REFRESH_EXPIRATION);
            res.put("refreshToken", newRefreshToken);
        } else {
            res.put("refreshToken", refreshToken);
        }

        return Response.success(res);
    }
}
