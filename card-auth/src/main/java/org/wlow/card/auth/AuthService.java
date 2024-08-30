package org.wlow.card.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 注册 <br>
     * 每种role下的用户名唯一
     */
    @Transactional
    public Response signUp(String username, String password, UserRole role) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        query.eq("role", role);
        if (userMapper.selectCount(query) > 0) {
            return Response.error("用户名已被占用");
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
    public Response login(String username, String password, UserRole role) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        query.eq("role", role);
        User user = userMapper.selectOne(query);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Response.error("用户名或密码错误");
        }
        // 生成token, refreshToken存到redis并设置过期时间
        String accessToken = jwtUtil.getToken(user.getId(), user.getUsername(), user.getRole(), TokenType.ACCESS);
        String refreshToken = jwtUtil.getToken(user.getId(), user.getUsername(), user.getRole(), TokenType.REFRESH);
        // RefreshToken:role:userId
        redisUtil.set("RefreshToken:" + user.getRole() + ":" + user.getId(), refreshToken, jwtUtil.REFRESH_EXPIRATION);

        return Response.success(Map.of("userId", user.getId(), "username", user.getUsername(), "accessToken", accessToken, "refreshToken", refreshToken));
    }

    /**
     * 刷新token
     */
    public Response refresh(String refreshToken) {
        Claims payload;
        try {
            payload = jwtUtil.parseToken(refreshToken, TokenType.REFRESH);
        } catch (SignatureException e) {
            log.error("refreshToken解析失败: {}", e.getMessage());
            return Response.error("无效的refreshToken");
        } catch (ExpiredJwtException e) {
            log.error("refreshToken已过期: {}", e.getMessage());
            return Response.error("refreshToken已过期");
        }
        Integer userId = payload.get("userId", Integer.class);
        String username = payload.get("username", String.class);
        String role = payload.get("role", String.class);

        String refreshTokenCached = redisUtil.get("RefreshToken:" + role + ":" + userId);
        if (!refreshToken.equals(refreshTokenCached)) {
            // refreshToken不匹配
            return Response.error("无效的refreshToken");
        }
        // 新的accessToken
        String accessToken = jwtUtil.getToken(userId, username, UserRole.valueOf(role), TokenType.ACCESS);
        Map<String, String> res = new HashMap<>();
        res.put("accessToken", accessToken);
        Long expiration = redisUtil.getExpire("RefreshToken:" + role + ":" + userId);
        if (expiration <= jwtUtil.REFRESH_BOUND) {
            // 刷新refreshToken
            String newRefreshToken = jwtUtil.getToken(userId, username, UserRole.valueOf(role), TokenType.REFRESH);
            redisUtil.set("RefreshToken:" + role + ":" + userId, newRefreshToken, jwtUtil.REFRESH_EXPIRATION);
            res.put("refreshToken", newRefreshToken);
        } else {
            res.put("refreshToken", refreshToken);
        }

        return Response.success(res);
    }
}
