package org.wlow.card.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wlow.card.auth.service.UserService;
import org.wlow.card.data.data.DTO.Response;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    /**
     * 获取用户个人信息
     */
    @GetMapping("/getUserInfo")
    public Response getUserInfo() {
        return userService.getUserInfo();
    }
}
