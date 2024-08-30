package org.wlow.card.application.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/date")
    public Object date() {
        return LocalDateTime.now();
    }

    @RequestMapping("/date1")
    public Object date1(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dateTime) {
        return dateTime;
    }
}
