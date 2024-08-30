package org.wlow.card.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.wlow.card.data.data.DTO.Response;

import java.util.Arrays;

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
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("参数校验未通过: {}", Arrays.toString(e.getDetailMessageArguments()));
        return Response.failure(400, "参数校验未通过: " + Arrays.toString(e.getDetailMessageArguments()));
    }
}
