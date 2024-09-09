package org.wlow.card.auth.controller;

import jakarta.annotation.Resource;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;
import org.wlow.card.auth.service.UserService;
import org.wlow.card.data.data.DTO.DTOUser;
import org.wlow.card.data.data.DTO.Response;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    /**
     * 获取用户个人信息 <br>
     * 从redis缓存中获取
     */
    @GetMapping("/getUserInfo")
    public Response<DTOUser> getUserInfo() {
        return userService.getUserInfo();
    }

    /**
     * 更新用户个人信息, 目前能改用户名.
     */
    @PatchMapping("/updateUserInfo")
    public Response<String> updateUserInfo(@RequestParam
                                       @Length(min = 4, message = "用户名长度不得小于4")
                                       @Length(max = 30, message = "用户名长度不得大于30")
                                       String username) {
        return userService.updateUserInfo(username);
    }
}
