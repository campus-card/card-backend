package org.wlow.card.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.file.exception.FileUploadException;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class FileUtil {
    @Value("${file-service.server-url}")
    private String serverUrl;
    @Value("${file-service.local-path}")
    private String fileLocalPath;

    /**
     * 上传文件到本地指定路径并返回前端浏览器能直接访问的虚拟路径
     * @param file 要存储的文件
     * @param dirName file的本地父级文件夹名
     * @param fileName 存储在本地的文件名. 使用UUID生成
     * @param virtualPath 虚拟路径, 用于前端浏览器访问
     * @return 前端浏览器能直接访问的虚拟路径, 比如 <a href="http://localhost:8192/file/xxx.jpg">this</a>
     */
    public String putFile(MultipartFile file, String dirName, String fileName, String virtualPath) {
        // 单位是字节
        long size = file.getSize();
        log.info("size = {}", size);
        if (size == 0) {
            throw new FileUploadException("文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileUploadException("文件名为空");
        }
        // 获取文件扩展名, 会包含点号, 比如 .jpg
        int index = originalFilename.lastIndexOf('.');
        if (index == -1) {
            throw new FileUploadException("没有类型的文件");
        }
        String extname = originalFilename.substring(index);
        log.info("extname = {}", extname);
        
        // 保存文件到本地
        StringBuilder path = new StringBuilder(fileLocalPath);
        int fileLocalPathLength = path.length();
        // 确保父级文件夹存在
        File parentFile = new File(path.append("/").append(dirName).toString());
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new FileUploadException("父级目录创建异常");
        }
        try {
            file.transferTo(new File(path.append("/").append(fileName).append(extname).toString()));
        } catch (IOException e) {
            throw new FileUploadException("文件保存异常");
        }

        // 生成返回给前端的虚拟web路径
        path.replace(0, fileLocalPathLength, serverUrl + virtualPath);
        
        return path.toString();
    }
}
