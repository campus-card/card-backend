package org.wlow.card.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.data.DTO.DTOUser;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.User;
import org.wlow.card.data.mapper.UserMapper;

@Slf4j
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public Response getUserInfo() {
        // 从CurrentUser中获取由token解析出的用户信息
        User user = userMapper.selectById(CurrentUser.getId());
        if (user == null) {
            return Response.error("用户不存在");
        }
        return Response.success(DTOUser.fromPO(user));
    }
}
