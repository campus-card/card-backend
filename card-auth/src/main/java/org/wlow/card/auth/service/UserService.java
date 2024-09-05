package org.wlow.card.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wlow.card.auth.JWTUtil;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.data.DTO.DTOUser;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.User;
import org.wlow.card.data.data.constant.UserRole;
import org.wlow.card.data.mapper.UserMapper;
import org.wlow.card.data.redis.RedisUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

@Slf4j
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JWTUtil jwtUtil;

    public Response getUserInfo() {
        // 先尝试从redis缓存中获取用户信息
        int userId = CurrentUser.getId();
        String userInfo = redisUtil.get("UserInfo:" + userId);
        if (userInfo == null) {
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Response.error("用户不存在");
            }
            try {
                redisUtil.set("UserInfo:" + userId, objectMapper.writeValueAsString(DTOUser.fromPO(user)), jwtUtil.REFRESH_EXPIRATION);
            } catch (JsonProcessingException e) {
                log.error("写入redis缓存userinfo失败: {}", e.getMessage());
            }
            return Response.success(DTOUser.fromPO(user));
        }
        try {
            return Response.success(objectMapper.readValue(userInfo, DTOUser.class));
        } catch (JsonProcessingException e) {
            log.error("读取redis缓存userinfo失败: {}", e.getMessage());
            return Response.error("服务器错误");
        }
    }

    public Response updateUserInfo(String username) {
        int userId = CurrentUser.getId();
        UserRole role = CurrentUser.getRole();
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        query.eq("role", role);
        if (userMapper.exists(query)) {
            return Response.failure(400, "用户名已被占用");
        }
        User user = User.builder()
                .id(userId)
                .username(username)
                .build();
        userMapper.updateById(user);
        // 同时更新redis缓存的用户信息
        try {
            redisUtil.set("UserInfo:" + userId, objectMapper.writeValueAsString(DTOUser.fromPO(user)), jwtUtil.REFRESH_EXPIRATION);
        } catch (JsonProcessingException e) {
            log.error("更新redis缓存userinfo失败: {}", e.getMessage());
            return Response.error("服务器错误");
        }
        return Response.ok();
    }
}
