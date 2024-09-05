package org.wlow.card.data.data.PO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 博客, 现用作测试大文本数据存储
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Blog {
    private Integer id;
    private String content;
}
