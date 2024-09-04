package org.wlow.card.student;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wlow.card.data.data.DTO.DTOCard;
import org.wlow.card.data.data.DTO.DTOPage;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.Card;
import org.wlow.card.data.data.PO.Product;
import org.wlow.card.data.data.PO.PurchaseRecord;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.mapper.CardMapper;
import org.wlow.card.data.mapper.ProductMapper;
import org.wlow.card.data.mapper.PurchaseRecordMapper;
import org.wlow.card.student.exception.CampusCardNotFoundException;
import org.wlow.card.student.exception.PurchaseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;

@Service
public class StudentService {
    @Resource
    private CardMapper cardMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private PurchaseRecordMapper purchaseRecordMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Response registerCard(String password) {
        // 已经经过PermissionInterceptor拦截器了, UserRole只能为Student或Admin
        int studentId = CurrentUser.getId();
        QueryWrapper<Card> query = new QueryWrapper<>();
        query.eq("user_id", studentId);
        if (cardMapper.exists(query)) {
            return Response.failure(400, "已经注册过校园卡");
        }

        // cardId格式: 日期yyyyMMdd + studentId(前面补0补足5位)
        String cardId = String.format("%1$tY%1$tm%1$td%2$05d", Calendar.getInstance(), studentId);
        Card newCard = Card.builder()
                .userId(studentId)
                .password(passwordEncoder.encode(password))
                .cardId(cardId)
                .build();
        // balance和createTime在MySQL中有默认值
        int res = cardMapper.insert(newCard);
        if (res != 1) {
            return Response.failure(500, "注册校园卡失败");
        }
        // MyBatisPlus插入后会自动回填主键id, 直接返回即可
        newCard.setBalance(BigDecimal.ZERO);
        newCard.setCreateTime(LocalDateTime.now());
        return Response.success("开通成功", DTOCard.fromPO(newCard));
    }

    public Response getCardInfo() {
        int studentId = CurrentUser.getId();
        Card card = ensureCardExists(studentId);
        return Response.success(DTOCard.fromPO(card));
    }

    public Response recharge(BigDecimal amount) {
        int studentId = CurrentUser.getId();
        ensureCardExists(studentId);
        UpdateWrapper<Card> update = new UpdateWrapper<>();
        update.eq("user_id", studentId);
        int res = cardMapper.recharge(amount, update);
        if (res != 1) {
            return Response.failure(500, "充值失败");
        }
        return Response.ok();
    }

    // todo: 是否要设置为同步方法? synchronized?
    @Transactional(rollbackFor = Exception.class)
    public Response purchase(Integer productId, Integer count, String password) {
        int studentId = CurrentUser.getId();
        Card card = ensureCardExists(studentId);

        // 检查支付密码
        if (!passwordEncoder.matches(password, card.getPassword())) {
            return Response.failure(400, "支付密码错误");
        }

        // 检查商品状态
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return Response.failure(400, "商品不存在");
        }
        if (product.getStore() < count) {
            // count校验必须为正数, 所以这里也包含了count <= 0的情况
            return Response.failure(400, "商品库存不足");
        }

        // 校园卡扣费
        BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(count));
        if (card.getBalance().compareTo(amount) < 0) {
            return Response.failure(400, "校园卡余额不足");
        }
        UpdateWrapper<Card> update = new UpdateWrapper<>();
        update.eq("user_id", studentId);
        int res = cardMapper.consume(amount, update);
        if (res != 1) {
            // 程序错误, 而不是业务错误, 直接抛异常, 回滚事务
            throw new PurchaseException("校园卡扣费异常");
        }
        card.setBalance(card.getBalance().subtract(amount));

        // 商品库存减少, 销量增加
        int res1 = productMapper.updateStoreAndSales(productId, count);
        if (res1 != 1) {
            throw new PurchaseException("商品库存更新异常");
        }

        // 插入购买记录
        PurchaseRecord record = PurchaseRecord.builder()
                .studentId(studentId)
                .productId(productId)
                .shopId(product.getShopId())
                .count(count)
                .purchaseTime(LocalDateTime.now())
                .amount(amount)
                .balance(card.getBalance())
                .build();
        purchaseRecordMapper.insert(record);

        return Response.success(DTOCard.fromPO(card));
    }

    public Response getPurchaseRecord(Integer page, Integer pageSize) {
        int studentId = CurrentUser.getId();
        // ensureCardExists(studentId);
        QueryWrapper<PurchaseRecord> query = new QueryWrapper<>();
        query.eq("student_id", studentId);
        query.orderByDesc("purchase_time");
        Page<PurchaseRecord> recordPage = Page.of(page, pageSize);
        purchaseRecordMapper.selectPage(recordPage, query);
        return Response.success(new DTOPage<>(
                recordPage.getTotal(),
                recordPage.getPages(),
                recordPage.getRecords()
        ));
    }

    public Response getProductList(Integer page, Integer pageSize, Integer order, Boolean isAsc) {
        Page<Product> productPage = Page.of(page, pageSize);
        QueryWrapper<Product> query = new QueryWrapper<>();
        query.orderBy(true, isAsc, switch (order) {
            case 1 -> "upload_time";
            case 2 -> "price";
            case 3 -> "store";
            default -> "id";
        });
        productMapper.selectPage(productPage, query);
        return Response.success(new DTOPage<>(
                productPage.getTotal(),
                productPage.getPages(),
                productPage.getRecords()
        ));
    }

    /**
     * 确保用户已经注册校园卡
     */
    private Card ensureCardExists(int userId) {
        QueryWrapper<Card> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        Card card = cardMapper.selectOne(query);
        if (card == null) {
            throw new CampusCardNotFoundException("未注册校园卡");
        }
        return card;
    }
}
