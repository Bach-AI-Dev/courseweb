package com.baitapnhom.courseweb.controller;

import com.baitapnhom.courseweb.service.EnrollmentService;
import com.baitapnhom.courseweb.dto.response.EnrollmentResponse;
import com.baitapnhom.courseweb.dto.response.ApiResponse;
import com.baitapnhom.courseweb.dto.response.PagedEnrollmentResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Endpoints:
 * - POST   /api/courses/{courseId}/enroll - Đăng ký khóa học
 * - GET    /api/users/me/courses - Lấy danh sách khóa học
 * - GET    /api/users/me/courses-paginated - Lấy danh sách phân trang
 * - GET    /api/courses/{courseId}/enrollment-status - Kiểm tra status
 * - DELETE /api/courses/{courseId}/enrollment - Hủy đăng ký
 * - GET    /api/enrollments/{enrollmentId} - Lấy chi tiết enrollment
 */
@RestController
@RequestMapping("/api")
public class EnrollmentController {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);
    
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId, 
            @RequestParam @NotBlank(message = "INVALID_STUDENT_ID") String studentId) {
        
        logger.info("Request to enroll student {} in course {}", studentId, courseId);
        
        EnrollmentResponse enrollmentData = enrollmentService.enrollCourse(studentId, courseId);
        
        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Đăng ký khóa học thành công!");
        response.setResult(enrollmentData);
        
        logger.info("Successfully enrolled student {} to course {}", studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
  
    @GetMapping("/users/me/courses")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyCourses(
            @RequestParam @NotBlank(message = "INVALID_STUDENT_ID") String studentId) {
        
        logger.info("Request to get all courses for student {}", studentId);
        
        List<EnrollmentResponse> myCourses = enrollmentService.getMyCourses(studentId);
        
        ApiResponse<List<EnrollmentResponse>> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy danh sách thành công");
        response.setResult(myCourses);

        return ResponseEntity.ok(response);
    }  
  
    @GetMapping("/users/me/courses-paginated")
    public ResponseEntity<ApiResponse<PagedEnrollmentResponse>> getMyCoursePaginated(
            @RequestParam @NotBlank(message = "INVALID_STUDENT_ID") String studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Request to get paginated courses for student {} - page: {}, size: {}", 
                   studentId, page, size);
        
        PagedEnrollmentResponse myCourses = enrollmentService.getMyCoursesPaginated(studentId, page, size);
        
        ApiResponse<PagedEnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy danh sách thành công");
        response.setResult(myCourses);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/courses/{courseId}/enrollment-status")
    public ResponseEntity<ApiResponse<Boolean>> getEnrollmentStatus(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId,
            @RequestParam @NotBlank(message = "INVALID_STUDENT_ID") String studentId) {
        
        logger.info("Checking enrollment status for student {} in course {}", studentId, courseId);
        
        boolean isEnrolled = enrollmentService.checkEnrollmentStatus(studentId, courseId);
        
        ApiResponse<Boolean> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Kiểm tra trạng thái thành công");
        response.setResult(isEnrolled);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/courses/{courseId}/enrollment")
    public ResponseEntity<ApiResponse<Void>> cancelEnrollment(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId,
            @RequestParam @NotBlank(message = "INVALID_STUDENT_ID") String studentId) {
        
        logger.info("Request to cancel enrollment for student {} in course {}", studentId, courseId);
        
        enrollmentService.cancelEnrollment(studentId, courseId);
        
        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Hủy đăng ký thành công");
        response.setResult(null);
        
        logger.info("Successfully canceled enrollment for student {} in course {}", studentId, courseId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentById(
            @PathVariable @NotBlank(message = "INVALID_ENROLLMENT_ID") String enrollmentId,
            @RequestParam @NotBlank(message = "INVALID_STUDENT_ID") String studentId) {
        
        logger.info("Request to get enrollment {} for student {}", enrollmentId, studentId);
        
        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(enrollmentId, studentId);
        
        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy chi tiết thành công");
        response.setResult(enrollment);
        
        return ResponseEntity.ok(response);
    }
}
