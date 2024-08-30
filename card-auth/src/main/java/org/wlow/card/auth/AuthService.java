package org.wlow.card.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlow.card.data.data.PO.User;
import org.wlow.card.data.data.VO.Response;
import org.wlow.card.data.data.constant.UserRole;
import org.wlow.card.data.mapper.UserMapper;
import org.wlow.card.data.redis.RedisUtil;

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
     * 注册
     */
    @Transactional
    public Response signUp(String username, String password, UserRole role) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
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
    public Response login(String username, String password) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        User user = userMapper.selectOne(query);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Response.error("用户名或密码错误");
        }


        return Response.ok();
    }

    /**
     * 刷新token
     */
    public Response refreshToken(String refreshToken) {
        Claims payload;
        try {
            payload = jwtUtil.parseToken(refreshToken, TokenType.REFRESH);
        } catch (SignatureException e) {
            log.error("刷新token解析失败: {}", e.getMessage());
            return Response.error("token无效");
        }
        return Response.ok();
    }
}
