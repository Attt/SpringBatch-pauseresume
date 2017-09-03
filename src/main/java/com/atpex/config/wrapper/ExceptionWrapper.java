package com.atpex.config.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 统一异常处理
 * Created by Atpex on 2017/5/15.
 */
@RestControllerAdvice
public class ExceptionWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionWrapper.class);

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseWrapper.Response handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        LOGGER.warn("参数解析失败:{}", e.getMessage());
        return ResponseWrapper.ofFail("could_not_read_json");
    }


    /**
     * 404 - Not Found
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseWrapper.Response handleNoHandlerFoundException(NoHandlerFoundException e) {
        LOGGER.warn("无法找到当前请求:{}", e.getMessage());
        return ResponseWrapper.ofFail("request_method_not_found");
    }

    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseWrapper.Response handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        LOGGER.warn("不支持当前请求方法:{}", e.getMessage());
        return ResponseWrapper.ofFail("request_method_not_supported");
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseWrapper.Response handleHttpMediaTypeNotSupportedException(Exception e) {
        LOGGER.warn("不支持当前媒体类型:{}", e.getMessage());
        return ResponseWrapper.ofFail("content_type_not_supported");
    }

    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseWrapper.Response handleException(Exception e) {
        LOGGER.warn("服务运行异常:{}", e.getMessage());
        return ResponseWrapper.ofFail(e.getMessage());
    }

}
