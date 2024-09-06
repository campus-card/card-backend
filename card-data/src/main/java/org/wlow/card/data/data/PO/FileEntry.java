package org.wlow.card.data.data.PO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 上传文件的记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileEntry {
    private Integer id;
    /**
     * 上传者的用户id
     */
    private Integer userId;
    /**
     * 文件名, 不带父级路径和后缀名
     */
    private String filename;
    /**
     * 文件所在的父级路径(绝对路径)
     */
    private String directory;
    /**
     * 文件扩展名, 包含点号, 比如 .jpg
     */
    private String extname;
    /**
     * 文件大小, 单位是字节
     */
    private Long size;
    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
}
