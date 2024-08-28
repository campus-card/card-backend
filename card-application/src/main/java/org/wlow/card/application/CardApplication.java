package org.wlow.card.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.wlow.card")
public class CardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardApplication.class, args);
    }

}
