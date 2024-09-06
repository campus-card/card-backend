package org.wlow.card.file.exception;

/**
 * 文件系统异常
 */
public class FileSystemException extends RuntimeException {
    public FileSystemException(String message) {
        super(message);
    }
}
