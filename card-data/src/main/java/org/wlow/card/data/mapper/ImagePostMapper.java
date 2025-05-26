package org.wlow.card.data.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.*;
import org.wlow.card.data.data.PO.ImagePost;

import java.util.List;

@Mapper
public interface ImagePostMapper extends BaseMapper<ImagePost> {
    /**
     * 插入图片动态和类别的多对多关系
     */
    @Insert("INSERT INTO image_post_category (image_post_id, category_id) VALUES (#{imagePostId}, #{categoryId})")
    int insertImagePostCategory(Integer imagePostId, Integer categoryId);

    @Select("SELECT category_id FROM image_post_category WHERE image_post_id = #{imagePostId}")
    List<Integer> getCategoryByImagePostId(Integer imagePostId);

    @Select("select ip.*, concat(#{webUrlPrefix}, fe.filename, fe.extname) image_url from image_post ip " +
            "join (select distinct image_post_id " +
            "from image_post_category " +
            "where category_id in (#{categoryIds})) temp " +
            "on ip.id = temp.image_post_id " +
            "join file_entry fe on ip.image_id = fe.id " +
            "where ${ew.sqlSegment}")
    IPage<ImagePost> getImagePostsByCategoryIds(IPage<ImagePost> page, @Param(Constants.WRAPPER) Wrapper<ImagePost> wrapper, String categoryIds, String webUrlPrefix);

    @Delete("DELETE FROM image_post_category WHERE image_post_id = #{imagePostId}")
    int deleteImagePostCategoryByImagePostId(Integer imagePostId);
}
