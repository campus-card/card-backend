package org.wlow.card.application;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.Digits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.Blog;
import org.wlow.card.data.mapper.BlogMapper;
import org.wlow.card.data.redis.RedisUtil;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private BlogMapper blogMapper;
    @Resource
    private RedisUtil redisUtil;

    @RequestMapping("/hello")
    public Response hello(@RequestParam
                              @Digits(integer = 10, fraction = 2, message = "金额只能有两位小数, 整数部分最多10位")
                              BigDecimal amount) {

        return Response.success(amount);
    }

    @PostMapping("/addBlog")
    public Response addBlog(@RequestParam String content) {
        Blog blog = Blog.builder()
                .content(content)
                .build();
        blogMapper.insert(blog);
        redisUtil.set("Test:Blog:" + blog.getId(), blog.getContent());
        return Response.ok();
    }

    @GetMapping("/getBlog")
    public Response getBlog(@RequestParam Integer id) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            return Response.failure(400, "博客不存在");
        }
        return Response.success(blog.getContent());
    }

    @GetMapping("/getBlogCached")
    public Response getBlogCached(@RequestParam Integer id) {
        String content = redisUtil.get("Test:Blog:" + id);
        if (content == null) {
            return Response.failure(400, "博客不存在");
        }
        return Response.success(content);
    }
}
