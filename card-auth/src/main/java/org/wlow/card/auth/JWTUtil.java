package org.wlow.card.auth;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wlow.card.data.data.constant.UserRole;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {
    /* expiration下的时间单位都为秒 */
    @Value("${jwt.expiration.access-token}")
    public long EXPIRATION;
    @Value("${jwt.expiration.refresh-token}")
    public long REFRESH_EXPIRATION;
    /**
     * 刷新token的有效期上限.
     */
    @Value("${jwt.expiration.refresh-bound}")
    public long REFRESH_BOUND;

    private final SecretKey KEY;
    private final SecretKey REFRESH_KEY;

    /**
     * 因为要根据配置文件中的值来初始化KEY, 所以不能在声明KEY时直接用SECRET_KEY来创建KEY对象,
     * 因为@Value是构造函数之后才会执行, 于是key的赋值提前到构造函数中
     */
    public JWTUtil(@Value("${jwt.secret-key.access-token}") String SECRET_KEY,
                   @Value("${jwt.secret-key.refresh-token}") String REFRESH_SECRET_KEY) {
        KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        REFRESH_KEY = Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());
    }

    private String genToken(Map<String, Object> payload, long EXPIRATION, SecretKey key) {
        return Jwts.builder()
                .header()
                .add("alg", "HS256")
                .add("typ", "JWT")
                .and()
                .claims(payload)
                .signWith(key, Jwts.SIG.HS256)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .compact();
    }

    /**
     * 获取不同类型的token <br>
     * 枚举都设置为name
     */
    public String getToken(Integer userId, String username, UserRole role, TokenType type) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", userId);
        payload.put("username", username);
        payload.put("role", role.name());
        // token类型也放进payload里. 等于枚举类的name("ACCESS", "REFRESH")
        payload.put("type", type);
        if (type == TokenType.ACCESS) {
            return genToken(payload, EXPIRATION, KEY);
        } else {
            return genToken(payload, REFRESH_EXPIRATION, REFRESH_KEY);
        }
    }

    /**
     * 获取token的信息
     * @return token的payload, Claims继承了Map<String, Object>
     */
    public Claims parseToken(String token, TokenType type) {
        if (type == TokenType.ACCESS) {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } else {
            return Jwts.parser()
                    .verifyWith(REFRESH_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
    }
}
