package com.baitapnhom.courseweb.controller;

import com.baitapnhom.courseweb.dto.request.LessonProgressRequest;
import com.baitapnhom.courseweb.dto.response.CourseMemberProgressResponse;
import com.baitapnhom.courseweb.dto.response.CourseProgressResponse;
import com.baitapnhom.courseweb.dto.response.LessonProgressResponse;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.UserRepository;
import com.baitapnhom.courseweb.service.ILessonProgressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
public class LessonProgressController {
    
    private final ILessonProgressService progressService;
    @Autowired
    private UserRepository userRepository;
    // Sử dụng Constructor Injection an toàn thay cho @RequiredArgsConstructor
    public LessonProgressController(ILessonProgressService progressService) {
        this.progressService = progressService;
    }
    private String getLoggedInStudentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 1. Lấy Username từ token (vd: "HaiNammm")
        String username = authentication.getName();

        // 2. Chọc vào DB để lấy ra ID thực sự (UUID) của Username này
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 3. Trả về UUID chuẩn xác (vd: "eb15546a-2c29...")
        return user.getId();
    }
    @PostMapping("/update")
    public ResponseEntity<LessonProgressResponse> updateProgress(@Valid @RequestBody LessonProgressRequest request) {

        // Lấy ID thật của người đang cầm Token
        String realStudentId = getLoggedInStudentId();



        LessonProgressResponse responseData = progressService.updateProgress(request,realStudentId);
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

    @GetMapping("/courses/{courseId}/members")
    public ResponseEntity<List<CourseMemberProgressResponse>> getCourseMembersProgress(
            @PathVariable String courseId) {
        return ResponseEntity.ok(progressService.getCourseMembersProgress(courseId));
    }
}

