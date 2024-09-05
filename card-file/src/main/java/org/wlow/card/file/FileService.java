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

    public Response putFile(MultipartFile file) {
        UUID uuid = UUID.randomUUID();
        // fileVirtualPath后面的"/**"要去掉
        String webFilePath = fileUtil.putFile(file, "file", uuid.toString(), fileVirtualPath.substring(0, fileVirtualPath.length() - 3));
        return Response.success(webFilePath);
    }
}
