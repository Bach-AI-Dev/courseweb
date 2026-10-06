package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.CourseRequest;
import com.baitapnhom.courseweb.dto.response.CourseResponse;
import com.baitapnhom.courseweb.entity.Category;
import com.baitapnhom.courseweb.entity.Course;
import com.baitapnhom.courseweb.enums.CourseStatus;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.CategoryRepository;
import com.baitapnhom.courseweb.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. Tạo mới khóa học
    public CourseResponse createCourse(CourseRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Course course = new Course();
        course.setTeacherId(request.getTeacherId());
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPrice(request.getPrice());
        course.setStatus(request.getStatus() != null ? request.getStatus() : CourseStatus.DRAFT);
        course.setCategory(category);

        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    // 2. Lấy toàn bộ danh sách
    public List<CourseResponse> getAllCourses() {
        List<CourseResponse> responses = new ArrayList<>();
        for (Course course : courseRepository.findAll()) {
            responses.add(toResponse(course));
        }
        return responses;
    }

    // 3. Lấy chi tiết theo ID
    public CourseResponse getCourseById(String id) {
        Course course = findCourseById(id);
        return toResponse(course);
    }

    // 4. Cập nhật khóa học
    public CourseResponse updateCourse(String id, CourseRequest request) {
        Course course = findCourseById(id);

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            course.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            course.setStatus(request.getStatus());
        }
        if (request.getThumbnailUrl() != null) {
            course.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            course.setCategory(category);
        }

        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    // 5. Xóa khóa học
    public void deleteCourse(String id) {
        Course course = findCourseById(id);

        if (courseRepository.countLessonsByCourseId(id) > 0) {
            throw new AppException(ErrorCode.CANNOT_DELETE_COURSE_HAS_LESSONS);
        }

        if (courseRepository.countEnrollmentsByCourseId(id) > 0) {
            throw new AppException(ErrorCode.CANNOT_DELETE_COURSE_HAS_ENROLLMENTS);
        }

        courseRepository.delete(course);
    }

    // Hàm nội bộ tìm Course theo ID
    private Course findCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
    }

    // Map Entity -> DTO Response
    private CourseResponse toResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTeacherId(course.getTeacherId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setThumbnailUrl(course.getThumbnailUrl());
        response.setPrice(course.getPrice());
        response.setStatus(course.getStatus());
        if (course.getCategory() != null) {
            response.setCategoryId(course.getCategory().getId());
            response.setCategoryName(course.getCategory().getName());
        }
        return response;
    }
}