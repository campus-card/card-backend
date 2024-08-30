package org.wlow.card.auth;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.constant.UserRole;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource
    private AuthService authService;

    @PostMapping("/signUp")
    public Response signUp(@RequestParam
                               @Min(value = 4, message = "用户名长度不得小于4")
                               @Max(value = 30, message = "用户名长度不得大于30")
                               String username,
                           @RequestParam
                               @Min(value = 6, message = "密码长度不得小于6")
                               String password,
                           @RequestParam UserRole role) {
        return authService.signUp(username, password, role);
    }

    @PostMapping("/login")
    public Response login(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam UserRole role) {
        return authService.login(username, password, role);
    }

    @PostMapping("refresh")
    public Response refresh(@RequestParam String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
