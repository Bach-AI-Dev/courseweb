package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.CourseRequest;
import com.baitapnhom.courseweb.dto.response.CourseResponse;
import com.baitapnhom.courseweb.entity.Category;
import com.baitapnhom.courseweb.entity.Course;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.enums.CourseStatus;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.CategoryRepository;
import com.baitapnhom.courseweb.repository.CourseRepository;
import com.baitapnhom.courseweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public CourseResponse createCourse(CourseRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Course course = new Course();
        course.setTeacher(teacher);
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPrice(request.getPrice());
        course.setStatus(request.getStatus() != null ? request.getStatus() : CourseStatus.DRAFT);
        course.setCategory(category);

        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    public List<CourseResponse> getAllCourses() {
        List<CourseResponse> responses = new ArrayList<>();
        for (Course course : courseRepository.findAll()) {
            responses.add(toResponse(course));
        }
        return responses;
    }

    public CourseResponse getCourseById(String id) {
        Course course = findCourseById(id);
        return toResponse(course);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
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
        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            course.setTeacher(teacher);
        }

        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    private Course findCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
    }

    private CourseResponse toResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        if (course.getTeacher() != null) {
            response.setTeacherId(course.getTeacher().getId());
        }
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