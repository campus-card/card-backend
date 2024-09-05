package org.wlow.card.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类, 扫描所有软件包 <br>
 * note: 其中@Mapper需要额外用@MapperScan指定扫描的mapper包
 */
@SpringBootApplication(scanBasePackages = {"org.wlow.card"})
@MapperScan("org.wlow.card.data.mapper")
public class CardApplication {
    public static void main(String[] args) {
        SpringApplication.run(CardApplication.class, args);
    }
}
