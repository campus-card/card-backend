package org.wlow.card.file;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.PO.FileEntry;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.mapper.FileEntryMapper;
import org.wlow.card.file.exception.FileSystemException;
import org.wlow.card.file.exception.FileUploadException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class FileUtil {
    @Value("${file-service.server-url}")
    private String serverUrl;
    @Value("${file-service.local-path.root}")
    private String fileLocalPath;

    @Resource
    private FileEntryMapper fileEntryMapper;

    /**
     * 上传文件到本地指定路径并返回前端浏览器能直接访问的虚拟路径
     * @param file 要存储的文件
     * @param dirname file的本地父级文件夹名
     * @param filename 存储在本地的文件名. 使用UUID生成
     * @param virtualPath 虚拟路径, 用于前端浏览器访问
     * @return FileEntry对象, 其webPath属性为前端浏览器能直接访问的虚拟路径, 比如 <a href="http://localhost:8192/file/xxx.jpg">this</a>
     */
    public FileEntry putFile(MultipartFile file, String dirname, String filename, String virtualPath) {
        if (file.isEmpty()) {
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
        
        // 保存文件到本地
        StringBuilder path = new StringBuilder(fileLocalPath);
        int fileLocalPathLength = path.length();
        // 确保父级文件夹存在
        File parentDir = new File(path.append(dirname).toString());
        // 父级文件夹的真实绝对路径, 不包含相对路径
        String parentDirAbsolutePath;
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new FileUploadException("父级目录创建异常");
        }

        // 保存文件到本地
        try {
            parentDirAbsolutePath = parentDir.getCanonicalPath();
            file.transferTo(new File(path.append("/").append(filename).append(extname).toString()).getCanonicalFile());
        } catch (IOException e) {
            throw new FileUploadException("文件保存异常: " + e.getMessage());
        }

        // 记录文件上传信息
        FileEntry fileEntry = FileEntry.builder()
                .userId(CurrentUser.getId())
                .filename(filename)
                .directory(parentDirAbsolutePath)
                .extname(extname)
                // 单位是字节
                .size(file.getSize())
                .uploadTime(LocalDateTime.now())
                // 生成返回给前端的虚拟web路径
                .webPath(path.replace(0, fileLocalPathLength, serverUrl + virtualPath).toString())
                .build();
        fileEntryMapper.insert(fileEntry);
        
        return fileEntry;
    }

    public boolean deleteFile(FileEntry fileEntry) {
        File file = new File(fileEntry.getDirectory() + "/" + fileEntry.getFilename() + fileEntry.getExtname());
        boolean deleteFile = file.delete();
        if (!deleteFile) {
            throw new FileSystemException("本地文件删除失败: " + fileEntry.getDirectory() + "/" + fileEntry.getFilename() + fileEntry.getExtname());
        }
        int deleteEntry = fileEntryMapper.deleteById(fileEntry.getId());
        if (deleteEntry != 1) {
            throw new FileSystemException("数据库文件记录删除失败: " + fileEntry.getId());
        }
        return true;
    }
}
