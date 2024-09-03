package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.wlow.card.data.data.PO.PurchaseRecord;

@Mapper
public interface PurchaseRecordMapper extends BaseMapper<PurchaseRecord> {
    /**
     * 商家查看自己商品的销售记录. <br>
     * 在自定义SQL中也可以使用分页插件. 传入并返回IPage即可
     */
    @Select("select pr.* from " +
            "purchase_record pr join product on product_id = product.id " +
            "                   join user on product.shop_id = user.id " +
            "where user.id = #{shopId}")
    IPage<PurchaseRecord> selectByShopId(Integer shopId, IPage<PurchaseRecord> page);
}
