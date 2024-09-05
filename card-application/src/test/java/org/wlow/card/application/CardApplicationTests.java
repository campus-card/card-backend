package org.wlow.card.application;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wlow.card.auth.TokenType;
import org.wlow.card.auth.JWTUtil;
import org.wlow.card.data.data.PO.Card;
import org.wlow.card.data.data.PO.PurchaseRecord;
import org.wlow.card.data.data.PO.User;
import org.wlow.card.data.data.constant.UserRole;
import org.wlow.card.data.mapper.CardMapper;
import org.wlow.card.data.mapper.ProductMapper;
import org.wlow.card.data.mapper.PurchaseRecordMapper;
import org.wlow.card.data.mapper.UserMapper;
import org.wlow.card.data.redis.RedisUtil;

import java.math.BigDecimal;

@Slf4j
@SpringBootTest
class CardApplicationTests {
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CardMapper cardMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private PurchaseRecordMapper purchaseRecordMapper;
    @Resource
    private RedisUtil redisUtil;

    @Test
    void token() {
        String token = jwtUtil.getToken(1, "admin", UserRole.Student, TokenType.ACCESS);
        log.info("token: {}", token);
        // 把access当做refresh解析时抛出SignatureException
        Claims res = jwtUtil.parseToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiU3R1ZGVudCIsImlkIjo1LCJ0eXBlIjoiQUNDRVNTIiwidXNlcm5hbWUiOiJyZWlzZW4iLCJleHAiOjE3MjU2MDY3NTd9.PS2xxabfXRD81yVbtWQzI7ppEbczSsTZDV0qOJ3wJi8", TokenType.ACCESS);
        log.info("payload: {}", res);
        log.info("expiration: {}", res.getExpiration());
    }

    @Test
    void enumTest() {
        User user = User.builder()
                .id(7)
                .username("Shop")
                .build();
        log.info("user: {}", user);
        userMapper.updateById(user);
    }

    @Test
    void mapper() {
        IPage<PurchaseRecord> recordPage = Page.of(1, 10);
        QueryWrapper<PurchaseRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("shop.id", 6);
        wrapper.orderByDesc("purchase_time");
        recordPage = purchaseRecordMapper.selectPurchaseRecord(recordPage, wrapper);
        log.info("records: {}", recordPage.getRecords());
        log.info("total: {}", recordPage.getTotal());
        log.info("pages: {}", recordPage.getPages());
    }

    @Test
    void update() {
        UpdateWrapper<Card> update = new UpdateWrapper<>();
        update.eq("user_id", 0);
        BigDecimal amount = BigDecimal.valueOf(123.45);
        cardMapper.recharge(amount, update);
    }

    @Test
    void redis() {
        String res = redisUtil.get("123");
        log.info("res: {}", res);
    }
}
