package org.wlow.card.data.data.PO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wlow.card.data.data.constant.UserRole;

import java.time.LocalDateTime;

/**
 * 用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Integer id;
    private String username;
    /**
     * 密码, 已加密
     */
    private String password;
    /**
     * 用户类型. 1: 学生, 2: 商家, 3: 管理员
     */
    private UserRole role;
    private LocalDateTime registerTime;
}
