package org.wlow.card.data.data.PO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Double price;
    /**
     * 商品库存
     */
    private Integer store;
    /**
     * 商品上架时间
     */
    private LocalDateTime uploadTime;
    /**
     * (最近一次)商品信息更新时间
     */
    private LocalDateTime modifyTime;
}
