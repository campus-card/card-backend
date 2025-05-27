package org.wlow.card.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI springOpenAPI() {
        // http://localhost:8192/campusCard/swagger-ui/index.html
        // http://server:port/context-path/swagger-ui.html
        // JSON格式信息: http://server:port/context-path/v3/api-docs
        return new OpenAPI().info(new Info()
                .title("校园卡消费系统")
                .description("校园卡消费系统 API 文档")
                .version("0.0.1"));
    }
}
