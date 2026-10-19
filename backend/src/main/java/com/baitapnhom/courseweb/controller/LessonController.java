package com.baitapnhom.courseweb.controller;

import com.baitapnhom.courseweb.dto.request.LessonRequest;
import com.baitapnhom.courseweb.dto.response.ApiResponse;
import com.baitapnhom.courseweb.dto.response.LessonResponse;
import com.baitapnhom.courseweb.service.LessonService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Endpoints:
 * - GET    /api/courses/{courseId}/lessons - Lấy danh sách bài học của khóa học
 * (POST/PUT/DELETE cần token TEACHER hoặc ADMIN)
 * - POST   /api/courses/{courseId}/lessons - Thêm bài học mới vào khóa học
 * - GET    /api/lessons/{id}               - Lấy chi tiết 1 bài học
 * - PUT    /api/lessons/{id}               - Cập nhật bài học
 * - DELETE /api/lessons/{id}               - Xóa bài học
 */
@RestController
@RequestMapping("/api")
public class LessonController {
    private static final Logger logger = LoggerFactory.getLogger(LessonController.class);

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsByCourse(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId) {

        logger.info("Request to get lessons of course {}", courseId);

        List<LessonResponse> lessons = lessonService.getLessonsByCourseId(courseId);

        ApiResponse<List<LessonResponse>> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy danh sách bài học thành công");
        response.setResult(lessons);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @PathVariable @NotBlank(message = "INVALID_COURSE_ID") String courseId,
            @Valid @RequestBody LessonRequest request) {

        logger.info("Request to create lesson in course {}", courseId);

        LessonResponse savedLesson = lessonService.createLesson(courseId, request);

        ApiResponse<LessonResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Thêm bài học thành công!");
        response.setResult(savedLesson);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/lessons/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(
            @PathVariable @NotBlank(message = "INVALID_LESSON_ID") String id) {

        logger.info("Request to get lesson {}", id);

        LessonResponse lesson = lessonService.getLessonById(id);

        ApiResponse<LessonResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Lấy chi tiết bài học thành công");
        response.setResult(lesson);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/lessons/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable @NotBlank(message = "INVALID_LESSON_ID") String id,
            @Valid @RequestBody LessonRequest request) {

        logger.info("Request to update lesson {}", id);

        LessonResponse updatedLesson = lessonService.updateLesson(id, request);

        ApiResponse<LessonResponse> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Cập nhật bài học thành công");
        response.setResult(updatedLesson);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/lessons/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @PathVariable @NotBlank(message = "INVALID_LESSON_ID") String id) {

        logger.info("Request to delete lesson {}", id);

        lessonService.deleteLesson(id);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(1000);
        response.setMessage("Xóa bài học thành công");

        return ResponseEntity.ok(response);
    }
}
