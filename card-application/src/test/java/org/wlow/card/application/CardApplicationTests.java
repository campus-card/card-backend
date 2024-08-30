package org.wlow.card.application;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wlow.card.auth.TokenType;
import org.wlow.card.auth.JWTUtil;
import org.wlow.card.data.data.PO.User;
import org.wlow.card.data.data.constant.UserRole;
import org.wlow.card.data.mapper.UserMapper;

@Slf4j
@SpringBootTest
class CardApplicationTests {
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private UserMapper userMapper;

    @Test
    void token() {
        String token = jwtUtil.getToken(1, "admin", UserRole.Student, TokenType.ACCESS);
        log.info("token: {}", token);
        // 把access当做refresh解析时抛出SignatureException
        Claims res = jwtUtil.parseToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiU3R1ZGVudCIsInR5cGUiOiJBQ0NFU1MiLCJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJleHAiOjE3MjUwMjUzODZ9.6kE2aM3yLc22Y1hgMLhB6P1sPLEfuhd1LiPsutuOr6I", TokenType.ACCESS);
        log.info("payload: {}", res);
        log.info("expiration: {}", res.getExpiration());
    }

    @Test
    void enumTest() {
        log.info("role: {}", UserRole.Admin);
    }

    @Test
    void mapper() {
        log.info("userMapper: {}", userMapper);
        User user = User.builder().username("reisen").password("123456").role(UserRole.Admin).build();
        userMapper.insert(user);
    }
}
