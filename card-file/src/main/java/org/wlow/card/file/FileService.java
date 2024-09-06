package org.wlow.card.file;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.Response;

import java.util.UUID;

@Service
public class FileService {
    @Resource
    private FileUtil fileUtil;
    @Value("${file-service.virtual-path.file}")
    private String fileVirtualPath;
    @Value("${file-service.virtual-path.image}")
    private String imageVirtualPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public Response putFile(MultipartFile file) {
        return saveFile(file, "file", fileVirtualPath);
    }

    public Response putImage(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Response.failure(400, "非图片文件");
        }
        return saveFile(image, "image", imageVirtualPath);
    }

    private Response saveFile(MultipartFile file, String dirname, String virtualPath) {
        UUID uuid = UUID.randomUUID();
        // fileVirtualPath后面的"/**"要去掉
        // 静态资源访问仍会收到contextPath的影响, 需要在虚拟路径前面加上contextPath
        String webFilePath = fileUtil.putFile(file, dirname, uuid.toString(), contextPath + virtualPath.substring(0, virtualPath.length() - 3));
        return Response.success(webFilePath);
    }
}
