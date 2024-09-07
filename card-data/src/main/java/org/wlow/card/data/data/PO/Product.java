package org.wlow.card.data.data.PO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private Integer id;
    /**
     * 商品所属商家id
     */
    private Integer shopId;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 商品图片对应的 {@link FileEntry} 的id, 不需要返回给前端
     */
    @JsonIgnore
    private Integer coverId;
    /**
     * 商品封面图片的url
     */
    @TableField(exist = false)
    private String coverUrl;
    private BigDecimal price;
    /**
     * 商品库存
     */
    private Integer store;
    /**
     * 商品销量
     */
    private Integer sales;
    /**
     * 商品上架时间
     */
    private LocalDateTime uploadTime;
    /**
     * (最近一次)商品信息更新时间
     */
    private LocalDateTime modifyTime;
}
