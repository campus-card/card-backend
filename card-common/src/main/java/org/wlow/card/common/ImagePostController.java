package org.wlow.card.common;

import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.DTOPage;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.ImagePost;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/imagePost")
public class ImagePostController {

    @Resource
    private ImagePostService imagePostService;

    /**
     * 上传图片动态
     *
     * @param title       图片动态的标题
     * @param description 图片动态的描述
     * @param categoryIds 图片动态的类别id列表
     * @param image       图片文件
     */
    @PostMapping("/upload")
    public Response<ImagePost> upload(@RequestParam @NotBlank String title,
                                      @RequestParam @NotBlank String description,
                                      @RequestParam @NotEmpty List<Integer> categoryIds,
                                      @RequestParam MultipartFile image
                                      ) {
        return imagePostService.uploadImagePost(title, description, categoryIds, image);
    }

    /**
     * 获取单个图片动态
     *
     * @param id 图片动态的id
     * @return 图片动态的详细信息
     */
    @GetMapping("/{id}")
    public Response<ImagePost> getImagePost(@PathVariable Integer id) {
        return imagePostService.getImagePostById(id);
    }

    /**
     * 根据类别搜索图片动态
     */
    @GetMapping("/search")
    public Response<DTOPage<ImagePost>> searchByCategory(@RequestParam @NotEmpty List<Integer> categoryIds,
                                                         @RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                         @RequestParam(defaultValue = "1")
                                                         @Positive(message = "排序字段必须为正数")
                                                         Integer order,
                                                         @RequestParam(defaultValue = "false")
                                                      Boolean isAsc) {
        return imagePostService.searchImagePostsByCategory(categoryIds, page, pageSize, order, isAsc);
    }

    /**
     * 删除图片动态
     */
    @DeleteMapping("/deleteImage")
    public Response<String> deleteImagePost(@RequestParam @Positive Integer id) {
        return imagePostService.deleteImagePost(id);
    }

    /**
     * 修改图片动态信息
     */
    @PostMapping("/modifyImagePost")
    public Response<ImagePost> modifyImagePost(@RequestParam @Positive Integer id,
                                               @RequestParam(required = false) @NotBlank String title,
                                               @RequestParam(required = false) @NotBlank String description,
                                               @RequestParam(required = false) List<Integer> categoryIds,
                                               @RequestParam(required = false) MultipartFile image) {
        return imagePostService.modifyImagePost(id, title, description, categoryIds, image);

    }


    /**
     * 下载图片
     * @param imagePostId 图片对应的 {@link ImagePost#imageId}
     */
    @GetMapping("/download/{imagePostId}")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable Integer imagePostId) throws FileNotFoundException {
        return imagePostService.downloadImage(imagePostId);
    }
}
