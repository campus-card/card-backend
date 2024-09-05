package org.wlow.card.file.exception;

/**
 * 文件上传异常
 */
public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
}
