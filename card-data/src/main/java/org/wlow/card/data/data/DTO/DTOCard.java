package org.wlow.card.data.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wlow.card.data.data.PO.Card;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 校园卡信息DTO, 不包含密码
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOCard {
    private Integer id;
    private Integer userId;
    private String cardId;
    private BigDecimal balance;
    private LocalDateTime createTime;

    public static DTOCard fromPO(Card card) {
        return new DTOCard(card.getId(), card.getUserId(), card.getCardId(), card.getBalance(), card.getCreateTime());
    }
}
