package com.baitapnhom.courseweb.controller;

import com.baitapnhom.courseweb.dto.request.EnrollmentRequest;
import com.baitapnhom.courseweb.dto.response.ApiResponse;
import com.baitapnhom.courseweb.dto.response.CourseEnrollmentSummaryResponse;
import com.baitapnhom.courseweb.dto.response.EnrollmentResponse;
import com.baitapnhom.courseweb.dto.response.PagedEnrollmentResponse;
import com.baitapnhom.courseweb.enums.EnrollmentStatus;
import com.baitapnhom.courseweb.service.EnrollmentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EnrollmentController {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);

    private final EnrollmentService enrollmentService;

    // DẠNG 11: Đã xóa hoàn toàn UserRepository khỏi Controller để tuân thủ kiến trúc phân tầng
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId) {

        logger.info("Request to enroll in course {}", courseId);
        EnrollmentResponse enrollmentData = enrollmentService.enrollCourse(courseId);

        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Đăng ký khóa học thành công!");
        response.setResult(enrollmentData);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users/me/courses")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyCourses() {
        List<EnrollmentResponse> myCourses = enrollmentService.getMyCourses();

        ApiResponse<List<EnrollmentResponse>> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy danh sách thành công");
        response.setResult(myCourses);

        return ResponseEntity.ok(response);
    }

    // DẠNG 13: Bổ sung tham số lọc status (required = false để không làm lỗi Frontend cũ)
    @GetMapping("/users/me/courses-paginated")
    public ResponseEntity<ApiResponse<PagedEnrollmentResponse>> getMyCoursePaginated(
            @RequestParam(required = false) EnrollmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedEnrollmentResponse myCourses = enrollmentService.getMyCoursesPaginated(status, page, size);

        ApiResponse<PagedEnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy danh sách thành công");
        response.setResult(myCourses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/enrollment-status")
    public ResponseEntity<ApiResponse<Boolean>> getEnrollmentStatus(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId) {

        boolean isEnrolled = enrollmentService.checkEnrollmentStatus(courseId);

        ApiResponse<Boolean> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Kiểm tra trạng thái thành công");
        response.setResult(isEnrolled);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/courses/{courseId}/enrollment")
    public ResponseEntity<ApiResponse<Void>> cancelEnrollment(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId) {

        enrollmentService.cancelEnrollment(courseId);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Hủy đăng ký thành công");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentById(
            @PathVariable @NotBlank(message = "INVALID_ENROLLMENT_ID") String enrollmentId) {

        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(enrollmentId);

        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy chi tiết thành công");
        response.setResult(enrollment);

        return ResponseEntity.ok(response);
    }

    // Teacher/Admin: Xem danh sách tất cả học viên đăng ký trong 1 khóa học
    @GetMapping("/courses/{courseId}/enrollments")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentsByCourse(
            @PathVariable String courseId) {

        List<EnrollmentResponse> list = enrollmentService.getEnrollmentsByCourseId(courseId);

        ApiResponse<List<EnrollmentResponse>> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy danh sách học viên của khóa học thành công");
        response.setResult(list);

        return ResponseEntity.ok(response);
    }

    // Admin/Teacher: Cập nhật trạng thái đăng ký (ACTIVE -> COMPLETED / CANCELED)
    @PutMapping("/admin/enrollments/{enrollmentId}/status")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateEnrollmentStatus(
            @PathVariable String enrollmentId,
            @RequestBody @Valid EnrollmentRequest request) {

        EnrollmentResponse result = enrollmentService.updateEnrollmentStatus(enrollmentId, request.getStatus());

        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Cập nhật trạng thái đăng ký thành công");
        response.setResult(result);

        return ResponseEntity.ok(response);
    }

    // Admin: Xóa dữ liệu đăng ký khóa học của học viên
    @DeleteMapping("/admin/enrollments/{enrollmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEnrollment(
            @PathVariable @NotBlank(message = "INVALID_ENROLLMENT_ID") String enrollmentId) {

        enrollmentService.deleteEnrollmentById(enrollmentId);

        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setCode(1000);
        apiResponse.setMessage("Xóa dữ liệu đăng ký khóa học thành công!");

        return ResponseEntity.ok(apiResponse);
    }

    // =========================================================================
    // CÁC ENDPOINT MỚI PHỤC VỤ DEMO DẠNG 10, 16, 17, 18, 19, 20 TRÊN POSTMAN
    // =========================================================================

    // Dạng 10: Thống kê khóa học
    @GetMapping("/courses/{courseId}/enrollments/summary")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseEnrollmentSummaryResponse>> getCourseEnrollmentSummary(
            @PathVariable String courseId) {
        CourseEnrollmentSummaryResponse summary = enrollmentService.getCourseEnrollmentSummary(courseId);
        ApiResponse<CourseEnrollmentSummaryResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy thống kê khóa học thành công");
        response.setResult(summary);
        return ResponseEntity.ok(response);
    }

    // Dạng 16: Chuyển đổi khóa học
    @PostMapping("/enrollments/transfer")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> transferCourse(
            @RequestParam String fromCourseId,
            @RequestParam String toCourseId) {
        String studentId = enrollmentService.getCurrentLoggedInUser().getId();
        EnrollmentResponse result = enrollmentService.transferCourse(studentId, fromCourseId, toCourseId);
        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Chuyển đổi khóa học thành công");
        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    // Dạng 17: Ghi danh hàng loạt
    @PostMapping("/courses/{courseId}/enrollments/batch")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> enrollBatchStudents(
            @PathVariable String courseId,
            @RequestBody List<String> studentIds) {
        List<EnrollmentResponse> result = enrollmentService.enrollBatchStudents(courseId, studentIds);
        ApiResponse<List<EnrollmentResponse>> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Ghi danh hàng loạt thành công");
        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    // Dạng 18: Tự động xét duyệt hoàn thành khóa học
    @PostMapping("/courses/{courseId}/enrollments/evaluate")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> evaluateAndCompleteEnrollment(
            @PathVariable String courseId) {
        String studentId = enrollmentService.getCurrentLoggedInUser().getId();
        EnrollmentResponse result = enrollmentService.evaluateAndCompleteEnrollment(studentId, courseId);
        ApiResponse<EnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Xét duyệt hoàn thành khóa học thành công");
        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    // Dạng 19: Tìm kiếm & lọc học viên đa tiêu chí có phân trang
    @GetMapping("/courses/{courseId}/enrollments/search")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedEnrollmentResponse>> searchCourseEnrollments(
            @PathVariable String courseId,
            @RequestParam(required = false) EnrollmentStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedEnrollmentResponse result = enrollmentService.searchCourseEnrollments(
                courseId, status, keyword, fromDate, toDate, page, size
        );
        ApiResponse<PagedEnrollmentResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Tìm kiếm danh sách học viên thành công");
        response.setResult(result);
        return ResponseEntity.ok(response);
    }

    // Dạng 20: Thu hồi tự động các tài khoản đăng ký ảo không học
    @PostMapping("/courses/{courseId}/enrollments/revoke-inactive")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> revokeInactiveEnrollments(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "7") int maxInactiveDays) {
        int revokedCount = enrollmentService.revokeInactiveEnrollments(courseId, maxInactiveDays);
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Đã thu hồi " + revokedCount + " tài khoản không hoạt động");
        response.setResult(revokedCount);
        return ResponseEntity.ok(response);
    }
}