package com.baitapnhom.courseweb.controller;

import com.baitapnhom.courseweb.service.EnrollmentService;
import com.baitapnhom.courseweb.repository.UserRepository;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.dto.response.EnrollmentResponse;
import com.baitapnhom.courseweb.dto.response.ApiResponse;
import com.baitapnhom.courseweb.dto.response.PagedEnrollmentResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EnrollmentController {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);
    
    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;

    public EnrollmentController(EnrollmentService enrollmentService, UserRepository userRepository) {
        this.enrollmentService = enrollmentService;
        this.userRepository = userRepository;
    }

    // Hàm private để tự động lấy studentId từ Token
    private String getLoggedInStudentId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user.getId(); 
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId) {
        
        String studentId = getLoggedInStudentId();
        logger.info("Request to enroll student {} in course {}", studentId, courseId);
        
        EnrollmentResponse enrollmentData = enrollmentService.enrollCourse(studentId, courseId);
        
        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Đăng ký khóa học thành công!");
        response.setResult(enrollmentData);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
  
    @GetMapping("/users/me/courses")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyCourses() {
        String studentId = getLoggedInStudentId();
        
        List<EnrollmentResponse> myCourses = enrollmentService.getMyCourses(studentId);
        
        ApiResponse<List<EnrollmentResponse>> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy danh sách thành công");
        response.setResult(myCourses);

        return ResponseEntity.ok(response);
    }  
  
    @GetMapping("/users/me/courses-paginated")
    public ResponseEntity<ApiResponse<PagedEnrollmentResponse>> getMyCoursePaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        String studentId = getLoggedInStudentId();
        PagedEnrollmentResponse myCourses = enrollmentService.getMyCoursesPaginated(studentId, page, size);
        
        ApiResponse<PagedEnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy danh sách thành công");
        response.setResult(myCourses);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/courses/{courseId}/enrollment-status")
    public ResponseEntity<ApiResponse<Boolean>> getEnrollmentStatus(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId) {
        
        String studentId = getLoggedInStudentId();
        boolean isEnrolled = enrollmentService.checkEnrollmentStatus(studentId, courseId);
        
        ApiResponse<Boolean> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Kiểm tra trạng thái thành công");
        response.setResult(isEnrolled);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/courses/{courseId}/enrollment")
    public ResponseEntity<ApiResponse<Void>> cancelEnrollment(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId) {
        
        String studentId = getLoggedInStudentId();
        enrollmentService.cancelEnrollment(studentId, courseId);
        
        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Hủy đăng ký thành công");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentById(
            @PathVariable @NotBlank(message = "INVALID_ENROLLMENT_ID") String enrollmentId) {
        
        String studentId = getLoggedInStudentId();
        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(enrollmentId, studentId);
        
        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy chi tiết thành công");
        response.setResult(enrollment);
        
        return ResponseEntity.ok(response);
    }
}