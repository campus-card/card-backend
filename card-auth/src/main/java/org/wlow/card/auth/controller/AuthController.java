package org.wlow.card.auth.controller;

import jakarta.annotation.Resource;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wlow.card.auth.service.AuthService;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.constant.UserRole;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource
    private AuthService authService;

    /**
     * 注册
     */
    @PostMapping("/signUp")
    public Response signUp(@RequestParam
                               @Length(min = 4, message = "用户名长度不得小于4")
                               @Length(max = 30, message = "用户名长度不得大于30")
                               String username,
                           @RequestParam
                               @Length(min = 6, message = "密码长度不得小于6")
                               String password,
                           @RequestParam UserRole role) {
        return authService.signUp(username, password, role);
    }

    /**
     * 登录, 需要传入role(1-学生, 2-商户, 3-管理员)
     */
    @PostMapping("/login")
    public Response login(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam UserRole role) {
        return authService.login(username, password, role);
    }

    /**
     * 刷新token
     */
    @PostMapping("refresh")
    public Response refresh(@RequestParam String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
