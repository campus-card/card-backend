package org.wlow.card.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.wlow.card.data.data.DTO.Response;

import java.util.Arrays;

/**
 * 全局异常处理器. 处理一些常见的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalHandler {
    /**
     * 处理请求参数缺少/未传递的异常 <br>
     * {@link ResponseStatus}可以指定响应的HTTP状态码. 不加时默认为200
     * 但是加了又不指定value时会是500
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        log.error("缺少请求参数: {} {}", e.getParameterType(), e.getParameterName());
        return Response.failure(400, "缺少请求参数: " + e.getParameterType() + " " + e.getParameterName());
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Response handleIllegalArgumentException(IllegalArgumentException e){
        log.error("非法参数: {}", e.getMessage());
        return Response.failure(400, "非法参数: " + e.getMessage());
    }

    /**
     * 处理参数校验未通过异常 <br>
     * 如果是Controller中直接接收参数校验不通过, 抛出的是HandlerMethodValidationException异常 <br>
     * 如果是DTO类接收参数校验不通过, 抛出的是MethodArgumentNotValidException异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public Response handleMethodArgumentNotValidException(Exception e){
        String message;
        if (e instanceof MethodArgumentNotValidException e1) {
            message = Arrays.toString(e1.getDetailMessageArguments());
        } else if (e instanceof HandlerMethodValidationException e2) {
            message = Arrays.toString(e2.getDetailMessageArguments());
        } else {
            message = e.getMessage();
        }
        log.error("参数校验未通过: {}", message);
        return Response.failure(400, "参数校验未通过: " + message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        log.error("不支持的请求方法: {}", e.getMethod());
        return Response.failure(405, "不支持的请求方法: " + e.getMethod());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public Response handleMyBatisSystemException(MyBatisSystemException e){
        log.error("数据库异常: {}", e.getMessage());
        return Response.failure(500, "数据库异常: " + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        String parameterName = e.getName();
        String parameterType = e.getParameter().getParameterType().getName();
        log.error("参数类型不匹配: 参数 {} 应当为 {} 类型", parameterName, parameterType);
        return Response.failure(400, "参数类型不匹配: 参数 " + parameterName + " 应当为 " + parameterType + " 类型");
    }

    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e){
        log.error("服务端异常: {} >> {}", e.getClass(), e.getMessage());
        return Response.error("服务端异常: " + e.getMessage());
    }
}
