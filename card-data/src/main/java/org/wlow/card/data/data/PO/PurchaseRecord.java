package org.wlow.card.data.data.PO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购买记录. 用于记录学生购买商品的记录, 也用于商户查看自己的商品的销售情况
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRecord {
    private Integer id;
    /**
     * 购买商品的学生的id. 卡号直接用学生id去查
     */
    private Integer studentId;
    /**
     * 购买的商品的id
     */
    private Integer productId;
    /**
     * 购买的商品所在商户的id
     */
    private Integer shopId;
    /**
     * 购买的商品的数量
     */
    private Integer count;
    /**
     * 购买后商品剩余库存
     */
    private Integer store;
    /**
     * 购买时的商品价格
     */
    private BigDecimal price;
    /**
     * 购买时间
     */
    private LocalDateTime purchaseTime;
    /**
     * 购买时的花费金额. 不用productId去查询商品的价格再计算, 因为商品的价格可能会变动
     */
    private BigDecimal amount;
    /**
     * 购买之后的学生校园卡的余额
     */
    private BigDecimal balance;


    /* =====连表查询的信息===== */
    /**
     * 购买商品的学生的用户名
     */
    @TableField(exist = false)
    private String studentName;
    /**
     * 购买的商品的名字
     */
    @TableField(exist = false)
    private String productName;
    /**
     * 购买的商品所在商户的用户名
     */
    @TableField(exist = false)
    private String shopName;
}
