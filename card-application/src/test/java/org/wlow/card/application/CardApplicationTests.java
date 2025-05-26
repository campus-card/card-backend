package org.wlow.card.application;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.wlow.card.auth.TokenType;
import org.wlow.card.auth.JWTUtil;
import org.wlow.card.data.data.DTO.DTOPage;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.*;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.data.constant.UserRole;
import org.wlow.card.data.mapper.*;
import org.wlow.card.data.redis.RedisUtil;
import org.wlow.card.file.FileUtil;
import org.wlow.card.shop.ShopService;
import org.wlow.card.student.StudentService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
class CardApplicationTests {
    @Value("${file-service.server-url}")
    private String serverUrl;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${file-service.virtual-path.image}")
    private String imageVirtualPath;
    @Value("${file-service.local-path.dir.image}")
    private String imageLocalDir;

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
    @Resource
    private FileUtil fileUtil;
    @Resource
    private FileEntryMapper fileEntryMapper;
    @Resource
    private ShopService shopService;
    @Resource
    private StudentService studentService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ImagePostMapper imagePostMapper;

    @Test
    void test() throws IOException {

    }

    @Test
    void token() {

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
        Page<Product> productPage = Page.of(1, 10);
        QueryWrapper<Product> query = new QueryWrapper<>();
        query.eq("shop_id", 6);
        query.orderByDesc("upload_time");
        productMapper.selectPageWithCoverUrl(productPage, query, serverUrl + contextPath + imageVirtualPath + imageLocalDir + "/");
        log.info("productPage: {}", productPage.getRecords());
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

    @Test
    void file() {

    }

    @Test
    void shopTest() {
        Response<DTOPage<Product>> productList = studentService.getProductList(1, 10, 1, true);
        log.info("productList: {}", productList);
    }
}
