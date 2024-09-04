package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.wlow.card.data.data.PO.PurchaseRecord;

@Mapper
public interface PurchaseRecordMapper extends BaseMapper<PurchaseRecord> {
    /**
     * 商家查看自己商品的销售记录. <br>
     * 在自定义SQL中也可以使用分页插件. 传入并返回IPage即可 <br>
     */
    @Select("select pr.*, product.name product_name, shop.username shop_name, student.username student_name " +
            "from purchase_record pr " +
            "    join product on product_id = product.id " +
            "    join user shop on pr.shop_id = shop.id " +
            "    join user student on student_id = student.id " +
            "where ${ew.sqlSegment}")
    IPage<PurchaseRecord> selectPurchaseRecord(IPage<PurchaseRecord> page, @Param(Constants.WRAPPER) Wrapper<PurchaseRecord> wrapper);
}
