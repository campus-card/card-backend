package org.wlow.card.auth;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wlow.card.auth.interceptor.AuthInterceptor;
import org.wlow.card.auth.interceptor.PermissionInterceptor;

import java.util.List;

@Configuration
public class AuthConfig implements WebMvcConfigurer {
    @Resource
    private AuthInterceptor authInterceptor;
    @Resource
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(List.of(
                        "/**/auth/**",
                        "/**/test/**",
                        // 放行图片请求, 以便前端能够正常显示图片, 其他文件的请求暂时不放行
                        "/**/img/image/**",
                        "/**/error"
                ));
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**");
    }
}
