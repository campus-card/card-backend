package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wlow.card.data.data.PO.Blog;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
}
