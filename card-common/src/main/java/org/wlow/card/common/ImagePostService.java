package org.wlow.card.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.DTOPage;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.FileEntry;
import org.wlow.card.data.data.PO.ImagePost;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.mapper.CategoryMapper;
import org.wlow.card.data.mapper.FileEntryMapper;
import org.wlow.card.data.mapper.ImagePostMapper;
import org.wlow.card.file.FileService;
import org.wlow.card.file.exception.FileSystemException;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImagePostService {

    @Value("${file-service.server-url}")
    private String serverUrl;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${file-service.virtual-path.image}")
    private String imageVirtualPath;
    @Value("${file-service.local-path.dir.image}")
    private String imageLocalDir;

    @Resource
    private FileService fileService;
    @Resource
    private FileEntryMapper fileEntryMapper;
    @Resource
    private ImagePostMapper imagePostMapper;
    @Resource
    private CategoryMapper categoryMapper;

    @Transactional(rollbackFor = Exception.class)
    public Response<ImagePost> uploadImagePost(String title, String description, List<Integer> categoryIds, MultipartFile image) {
        FileEntry imageEntry = fileService.putImageEntry(image);
        ImagePost imagePost = ImagePost.builder()
                .title(title)
                .description(description)
                .imageId(imageEntry.getId())
                .imageUrl(imageEntry.getWebUrl())
                .userId(CurrentUser.getId())
                .uploadTime(LocalDateTime.now())
                .modifyTime(LocalDateTime.now())
                .browse(0L)
                .likes(0L)
                .build();
        imagePostMapper.insert(imagePost);

        // 保存图片和分类的多对多关系
        categoryIds.forEach(categoryId -> imagePostMapper.insertImagePostCategory(imagePost.getId(), categoryId));
        // 查询并返回图片的类别完整信息
        imagePost.setCategories(categoryMapper.selectBatchIds(categoryIds));
        return Response.success(imagePost);
    }

    public Response<ImagePost> getImagePostById(Integer id) {
        ImagePost imagePost = imagePostMapper.selectById(id);
        if (imagePost == null) {
            return Response.error("不存在的图片动态");
        }
        // 当前访问用户不是发布者时, 增加浏览量
        if (!CurrentUser.getId().equals(imagePost.getUserId())) {
            imagePostMapper.update(new UpdateWrapper<ImagePost>().setSql("browse = browse + 1").eq("id", id));
            imagePost.setBrowse(imagePost.getBrowse() + 1);
        }
        // 设置图片的访问URL
        FileEntry imageEntry = fileEntryMapper.selectById(imagePost.getImageId());
        String imageUrl = serverUrl + contextPath +
                imageVirtualPath + imageLocalDir + "/" + imageEntry.getFilename() +
                imageEntry.getExtname();
        imagePost.setImageUrl(imageUrl);
        // 查询并返回图片的类别完整信息
        List<Integer> categoryIds = imagePostMapper.getCategoryByImagePostId(id);
        imagePost.setCategories(categoryMapper.selectBatchIds(categoryIds));
        return Response.success(imagePost);
    }

    public Response<DTOPage<ImagePost>> searchImagePostsByCategory(List<Integer> categoryIds, Integer page, Integer pageSize, Integer order, Boolean isAsc) {
        IPage<ImagePost> imagePostPage = Page.of(page, pageSize);
        QueryWrapper<ImagePost> query = new QueryWrapper<>();
        query.eq("1", 1);
        query.orderBy(true, isAsc, switch (order) {
            case 1 -> "upload_time";
            case 2 -> "browse";
            case 3 -> "likes";
            default -> "upload_time"; // 默认按上传时间排序
        });
        String categoryIdsStr = String.join(",", categoryIds.stream().map(String::valueOf).toList());
        imagePostMapper.getImagePostsByCategoryIds(imagePostPage, query, categoryIdsStr,
                serverUrl + contextPath + imageVirtualPath + imageLocalDir + "/");

        // 补上类别信息
        imagePostPage.getRecords().forEach(imagePost -> {
            imagePost.setCategories(categoryMapper.selectBatchIds(imagePostMapper.getCategoryByImagePostId(imagePost.getId())));
        });
        return Response.success(new DTOPage<>(
                imagePostPage.getTotal(),
                imagePostPage.getPages(),
                imagePostPage.getRecords()
        ));
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<String> deleteImagePost(Integer id) {
        ImagePost imagePost = imagePostMapper.selectById(id);
        if (imagePost == null) {
            return Response.error("不存在的图片动态");
        }
        if (!CurrentUser.getId().equals(imagePost.getUserId())) {
            return Response.error("不能删除其他用户的图片动态");
        }
        // 删除图片和分类的多对多关系
        imagePostMapper.deleteImagePostCategoryByImagePostId(id);
        // 删除图片文件
        FileEntry image = fileEntryMapper.selectById(id);
        if (image != null) {
            if (!fileService.deleteFile(image)) {
                throw new FileSystemException("删除本地图片文件失败");
            }
        }
        // 删除图片动态
        int res = imagePostMapper.deleteById(id);
        if (res == 1) {
            return Response.success("删除图片动态成功");
        } else {
            return Response.error("删除图片动态失败");
        }
    }

    public ResponseEntity<InputStreamResource> downloadImage(Integer imagePostId) throws FileNotFoundException {
        ImagePost imagePost = imagePostMapper.selectById(imagePostId);
        if (imagePost == null) {
            return ResponseEntity.notFound().build();
        }
        FileEntry imageEntry = fileEntryMapper.selectById(imagePost.getImageId());
        return fileService.downloadFile(imageEntry.getId());
    }
}
