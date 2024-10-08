package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.wlow.card.data.data.PO.Product;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Update("update product set store = store - #{count}, sales = sales + #{count} where id = #{id}")
    int updateStoreAndSales(Integer id, Integer count);

    /**
     * 查询商品信息, 并带上封面的url
     */
    @Select("select p.*, concat(#{webUrlPrefix}, fe.filename, fe.extname) cover_url " +
            "from product p " +
            "left join file_entry fe on p.cover_id = fe.id " +
            "where p.id = #{id}")
    Product selectByIdWithCoverUrl(Integer id, String webUrlPrefix);

    // note: 有的商品可能没有封面, 所以要用左连接 (数据库字段允许null导致的)
    @Select("select p.*, concat(#{webUrlPrefix}, fe.filename, fe.extname) cover_url " +
            "from product p " +
            "join file_entry fe on p.cover_id = fe.id " +
            "where ${ew.sqlSegment}")
    IPage<Product> selectPageWithCoverUrl(IPage<Product> page, @Param(Constants.WRAPPER) Wrapper<Product> wrapper, String webUrlPrefix);
}
