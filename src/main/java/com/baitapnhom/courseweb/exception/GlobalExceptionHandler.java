package com.baitapnhom.courseweb.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;

import com.baitapnhom.courseweb.dto.response.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        
        logger.error("AppException caught: Code={}, Message={}", 
                    errorCode.getCode(), exception.getMessage());
        
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(exception.getMessage());
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    // 2. Xử lý validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
        
        logger.error("Validation error: {}", enumKey);
        
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    // 3. Xử lý unique constraint violation
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<?>> handlingDataIntegrityViolation(DataIntegrityViolationException exception) {
        logger.error("Database constraint violation", exception);
        
        ApiResponse<?> apiResponse = new ApiResponse<>();
        // Trả về một mã lỗi chung cho dữ liệu (ví dụ: 9998) thay vì ép mã ALREADY_ENROLLED (1201)
        apiResponse.setCode(9998); 
        apiResponse.setMessage("Dữ liệu không hợp lệ hoặc đã tồn tại trong hệ thống (vi phạm ràng buộc).");
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    // 4. PHẢI ĐẶT SAU - Xử lý Exception tổng quát (catchall)
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingUncategoriedException(Exception exception) {
        logger.error("Uncategorized exception", exception);
        
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIED_EXISTED.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIED_EXISTED.getMessage());
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
