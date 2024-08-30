package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.wlow.card.data.data.PO.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
