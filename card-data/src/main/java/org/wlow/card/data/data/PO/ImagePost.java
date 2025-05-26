package org.wlow.card.data.data.PO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户分享的图片动态
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagePost {
    private Integer id;
    private Integer userId;
    /**
     * 图片动态的标题
     */
    private String title;
    private String description;
    /**
     * 图片对应的 {@link FileEntry} 的id, 需要返回给前端以便下载图片
     */
    private Integer imageId;
    @TableField(exist = false)
    private String imageUrl;
    /**
     * 图片浏览量
     */
    private Long browse;
    /**
     * 图片点赞量
     */
    private Long likes;
    private LocalDateTime uploadTime;
    private LocalDateTime modifyTime;

    /**
     * 图片的类型. 需要额外从数据库多对多关系表中查询
     */
    @TableField(exist = false)
    private List<Category> categories;
}
