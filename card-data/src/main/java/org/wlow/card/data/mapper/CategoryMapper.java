package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wlow.card.data.data.PO.Category;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
