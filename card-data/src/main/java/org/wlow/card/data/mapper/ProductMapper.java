package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.wlow.card.data.data.PO.Product;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Update("update product set store = store - #{count}, sales = sales + #{count} where id = #{id}")
    int updateStoreAndSales(Integer id, Integer count);
}
