package com.baitapnhom.courseweb.controller;

import com.baitapnhom.courseweb.dto.request.CourseRequest;
import com.baitapnhom.courseweb.dto.response.CourseResponse;
import com.baitapnhom.courseweb.service.CourseService;
import com.baitapnhom.courseweb.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonService lessonService;

    // 1. Tạo khóa học mới (POST)
    @PostMapping
    public CourseResponse createCourse(@RequestBody CourseRequest request) {
        return courseService.createRequest(request);
    }

    // 2. Lấy danh sách tất cả khóa học (GET)
    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    // 3. Lấy thông tin chi tiết 1 khóa học theo ID (GET)
    @GetMapping("/{id}")
    public CourseResponse getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id);
    }

    // 4. Cập nhật thông tin khóa học (PUT)
    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable String id, @RequestBody CourseRequest request) {
        return courseService.updateCourse(id, request);
    }

    // 5. Xóa khóa học theo ID (DELETE)
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable String id) {
        return courseService.deleteCourse(id) ;
    }
}

