package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.wlow.card.data.data.PO.Card;

import java.math.BigDecimal;

@Mapper
public interface CardMapper extends BaseMapper<Card> {
    /**
     * 充值
     */
    @Update("update card set balance = balance + #{amount} where ${ew.sqlSegment}")
    int recharge(BigDecimal amount, @Param(Constants.WRAPPER) Wrapper<Card> wrapper);

    /**
     * 消费
     */
    @Update("update card set balance = balance - #{amount} where ${ew.sqlSegment}")
    int consume(BigDecimal amount, @Param(Constants.WRAPPER) Wrapper<Card> wrapper);
}
