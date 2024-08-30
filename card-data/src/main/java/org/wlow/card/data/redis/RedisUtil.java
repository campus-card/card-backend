package org.wlow.card.data.redis;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Value("${redis-prefix}")
    private String prefix;

    /**
     * 存入
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(prefix + key, value);
    }

    public void set(String key, String value, long expiration) {
        redisTemplate.opsForValue().set(prefix + key, value, expiration, TimeUnit.SECONDS);
    }

    /**
     * 获取
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(prefix + key);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(prefix + key, TimeUnit.SECONDS);
    }

    /**
     * 删除
     */
    public void delete(String key) {
        redisTemplate.delete(prefix + key);
    }
}
