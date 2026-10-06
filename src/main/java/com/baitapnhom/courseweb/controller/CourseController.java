package com.baitapnhom.courseweb.controller;

import com.baitapnhom.courseweb.dto.request.CourseRequest;
import com.baitapnhom.courseweb.dto.response.ApiResponse;
import com.baitapnhom.courseweb.dto.response.CourseResponse;
import com.baitapnhom.courseweb.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 1. Tạo mới: Chỉ TEACHER hoặc ADMIN
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<CourseResponse> createCourse(@RequestBody @Valid CourseRequest request) {
        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.createCourse(request));
        return apiResponse;
    }

    // 2. Lấy danh sách: Mọi người đều xem được
    @GetMapping
    public ApiResponse<List<CourseResponse>> getAllCourses() {
        ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getAllCourses());
        return apiResponse;
    }

    // 3. Lấy chi tiết: Mọi người đều xem được
    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getCourseById(@PathVariable String id) {
        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.getCourseById(id));
        return apiResponse;
    }

    // 4. Sửa: Chỉ TEACHER hoặc ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<CourseResponse> updateCourse(@PathVariable String id, @RequestBody @Valid CourseRequest request) {
        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(courseService.updateCourse(id, request));
        return apiResponse;
    }

    // 5. Xóa: Chỉ ADMIN hoặc TEACHER
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ApiResponse<String> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult("Xóa khóa học thành công!");
        return apiResponse;
    }
}