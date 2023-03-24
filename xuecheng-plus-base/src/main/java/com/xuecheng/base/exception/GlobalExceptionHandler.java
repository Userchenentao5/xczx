package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.StringJoiner;

/**
 * @description 全局异常处理器
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

   /**
    * 权限校验异常
    */
   @ExceptionHandler(AccessDeniedException.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public RestErrorResponse handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
   {
      String requestURI = request.getRequestURI();
      log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
      return new RestErrorResponse(e.getMessage());
   }

   @ExceptionHandler(XueChengPlusException.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public RestErrorResponse customException(XueChengPlusException e) {
      log.error("【系统异常】{}",e.getErrMessage(),e);
      return new RestErrorResponse(e.getErrMessage());

   }

   @ExceptionHandler(Exception.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public RestErrorResponse exception(Exception e) {

      log.error("【系统异常】{}",e.getMessage(),e);
      return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
   }

   @ExceptionHandler(value = MethodArgumentNotValidException.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public RestErrorResponse doValidException(MethodArgumentNotValidException argumentNotValidException) {

      BindingResult bindingResult = argumentNotValidException.getBindingResult();
      StringJoiner errMsg = new StringJoiner(",");

      List<FieldError> fieldErrors = bindingResult.getFieldErrors();
      fieldErrors.forEach(error -> {
         errMsg.add(error.getDefaultMessage());
      });
      log.error(errMsg.toString());
      return new RestErrorResponse(errMsg.toString());
   }
}
