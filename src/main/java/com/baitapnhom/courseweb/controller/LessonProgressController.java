package com.baitapnhom.courseweb.controller;

import com.baitapnhom.courseweb.dto.request.LessonProgressRequest;
import com.baitapnhom.courseweb.dto.response.CourseProgressResponse;
import com.baitapnhom.courseweb.dto.response.LessonProgressResponse;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.service.ILessonProgressService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/progress")
public class LessonProgressController {
    
    private final ILessonProgressService progressService;

    // Sử dụng Constructor Injection an toàn thay cho @RequiredArgsConstructor
    public LessonProgressController(ILessonProgressService progressService) {
        this.progressService = progressService;
    }
    private String getLoggedInStudentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem request có token hợp lệ không (loại trừ trường hợp người dùng ẩn danh)
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // Bạn có thể đổi ErrorCode.UNAUTHENTICATED thành mã lỗi tương ứng trong Enum của bạn
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Trả về StudentId / Username lưu trong Token
        return authentication.getName();
    }
    @PostMapping("/update")
    public ResponseEntity<LessonProgressResponse> updateProgress(@Valid @RequestBody LessonProgressRequest request) {

        // Lấy ID thật của người đang cầm Token
        String realStudentId = getLoggedInStudentId();

        // Ghi đè ID vào request để chặn việc Frontend gửi linh tinh
        request.setStudentId(realStudentId);

        LessonProgressResponse responseData = progressService.updateProgress(request);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<CourseProgressResponse> getCourseProgress(@PathVariable String courseId) {

        // Lấy ID thật của người đang cầm Token
        String realStudentId = getLoggedInStudentId();

        CourseProgressResponse response = progressService.getCourseProgress(courseId, realStudentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lessons/{lessonId}/progress")
    public ResponseEntity<LessonProgressResponse> getLessonProgress(@PathVariable String lessonId) {

        // Lấy ID thật của người đang cầm Token
        String realStudentId = getLoggedInStudentId();

        LessonProgressResponse response = progressService.getLessonProgress(lessonId, realStudentId);
        return ResponseEntity.ok(response);
    }
}

