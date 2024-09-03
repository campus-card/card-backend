package org.wlow.card.data.data.PO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {
    private Integer id;
    /**
     * 校园卡所属的用户id. 对应用户的role一定是学生
     */
    private Integer userId;
    /**
     * 校园卡号, 目前采用日期+所属用户id补足5位数的方式
     */
    private String cardId;
    /**
     * 支付密码, 6位数字, 加密存储.
     */
    private String password;
    /**
     * 校园卡余额, 单位元. BigDecimal能直接存进MySQL的DECIMAL类型
     */
    private BigDecimal balance;
    private LocalDateTime createTime;
}
