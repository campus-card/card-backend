package org.wlow.card.auth;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wlow.card.auth.interceptor.AuthInterceptor;
import org.wlow.card.auth.interceptor.PermissionInterceptor;

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
                .excludePathPatterns("/**/auth/**")
                .excludePathPatterns("/**/test/**")
                .excludePathPatterns("/**/error");
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**");
    }
}
