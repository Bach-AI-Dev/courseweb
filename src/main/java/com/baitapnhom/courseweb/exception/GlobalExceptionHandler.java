package com.baitapnhom.courseweb.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;

import com.baitapnhom.courseweb.dto.response.ApiResponse;

// Nơi tập hợp tất cả các exception
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. PHẢI ĐẶT TRƯỚC - Xử lý AppException (cụ thể)
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse<>();
        
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(exception.getMessage());
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    // 2. Xử lý validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
        
        ApiResponse apiResponse = new ApiResponse<>();
        
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    // 3. Xử lý unique constraint violation
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<?>> handlingDataIntegrityViolation(DataIntegrityViolationException exception) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        
        apiResponse.setCode(1009);
        apiResponse.setMessage("Bạn đã đăng ký khóa học này rồi!");
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    // 4. PHẢI ĐẶT SAU - Xử lý Exception tổng quát (catchall)
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingUncategoriedException(Exception exception) {
        ApiResponse apiResponse = new ApiResponse<>();
        
        apiResponse.setCode(ErrorCode.UNCATEGORIED_EXISTED.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIED_EXISTED.getMessage());
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
