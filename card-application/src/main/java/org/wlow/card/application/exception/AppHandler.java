package org.wlow.card.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.file.exception.FileSystemException;
import org.wlow.card.file.exception.FileUploadException;
import org.wlow.card.student.exception.CampusCardNotFoundException;
import org.wlow.card.student.exception.PurchaseException;

/**
 * 应用异常处理器. 处理应用中的异常
 */
@Slf4j
@RestControllerAdvice
public class AppHandler {
    /**
     * 未查询到校园卡. 往往是因为学生未注册校园卡
     */
    @ExceptionHandler(CampusCardNotFoundException.class)
    public Response handleCampusCardNotFoundException(CampusCardNotFoundException e) {
        log.warn("校园卡不存在: {}", e.getMessage());
        return Response.failure(404, "校园卡不存在: " + e.getMessage());
    }

    /**
     * 学生购买商品时出现异常
     */
    @ExceptionHandler(PurchaseException.class)
    public Response handlePurchaseException(PurchaseException e) {
        log.warn("商品购买异常: {}", e.getMessage());
        return Response.failure(400, "商品购买异常: " + e.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    public Response handleFileUploadException(FileUploadException e) {
        log.warn("文件上传异常: {}", e.getMessage());
        return Response.failure(400, "文件上传异常: " + e.getMessage());
    }

    @ExceptionHandler(FileSystemException.class)
    public Response handleFileSystemException(FileSystemException e) {
        log.warn("文件系统异常: {}", e.getMessage());
        return Response.error("文件系统异常: " + e.getMessage());
    }
}
