package org.wlow.card.application;

import jakarta.validation.constraints.Digits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wlow.card.data.data.DTO.Response;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/hello")
    public Response hello(@RequestParam
                              @Digits(integer = 10, fraction = 2, message = "金额只能有两位小数, 整数部分最多10位")
                              BigDecimal amount) {

        return Response.success(amount);
    }
}
