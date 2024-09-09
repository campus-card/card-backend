package org.wlow.card.file;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.FileEntry;
import org.wlow.card.file.exception.FileUploadException;

import java.util.UUID;

@Service
public class FileService {
    @Resource
    private FileUtil fileUtil;
    @Value("${file-service.virtual-path.file}")
    private String fileVirtualPath;
    @Value("${file-service.virtual-path.image}")
    private String imageVirtualPath;
    /**
     * 本地文件存储目录
     */
    @Value("${file-service.local-path.dir.file}")
    private String fileLocalDir;
    /**
     * 本地图片文件存储目录
     */
    @Value("${file-service.local-path.dir.image}")
    private String imageLocalDir;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 保存文件, 返回文件的虚拟路径
     */
    public Response<String> putFile(MultipartFile file) {
        FileEntry fileEntry = saveFile(file, fileLocalDir, fileVirtualPath);
        return Response.success(fileEntry.getWebUrl());
    }

    /**
     * 保存图片, 返回图片的虚拟路径
     */
    public Response<String> putImage(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Response.failure(400, "非图片文件");
        }
        FileEntry fileEntry = saveFile(image, imageLocalDir, imageVirtualPath);
        return Response.success(fileEntry.getWebUrl());
    }

    /**
     * 保存文件, 返回对应的FileEntry
     */
    public FileEntry putFileEntry(MultipartFile file) {
        return saveFile(file, fileLocalDir, fileVirtualPath);
    }

    /**
     * 保存图片, 返回对应的FileEntry
     */
    public FileEntry putImageEntry(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new FileUploadException("非图片文件");
        }
        return saveFile(image, imageLocalDir, imageVirtualPath);
    }

    private FileEntry saveFile(MultipartFile file, String dirname, String virtualPath) {
        UUID uuid = UUID.randomUUID();
        // 静态资源访问仍会收到contextPath的影响, 需要在虚拟路径前面加上contextPath
        return fileUtil.putFile(file, dirname, uuid.toString(), contextPath + virtualPath);
    }

    public boolean deleteFile(FileEntry fileEntry) {
        return fileUtil.deleteFile(fileEntry);
    }
}
