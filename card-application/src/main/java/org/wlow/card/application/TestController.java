package org.wlow.card.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/hello")
    public Object hello() {
        log.info("hello");
        return LocalDateTime.now();
    }
}
