package org.wlow.card.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置文件虚拟请求路径以及映射到的本地文件路径
 */
@Configuration
public class FileConfig implements WebMvcConfigurer {
    @Value("${file-service.local-path.root}")
    private String localPath;
    @Value("${file-service.virtual-path.file}")
    private String fileVirtualPath;
    @Value("${file-service.virtual-path.image}")
    private String imageVirtualPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(fileVirtualPath + "/**") // 虚拟请求路径
                .addResourceLocations("file:" + localPath) // 映射到的本地文件路径
                .resourceChain(true);
        // 单独为image配置虚拟请求路径, 因为要设置AuthInterceptor不拦截图片请求
        registry.addResourceHandler(imageVirtualPath + "/**")
                .addResourceLocations("file:" + localPath)
                .resourceChain(true);
    }
}
