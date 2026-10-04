package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.CourseRequest;
import com.baitapnhom.courseweb.dto.response.CourseResponse;
import com.baitapnhom.courseweb.entity.Category;
import com.baitapnhom.courseweb.entity.Course;
import com.baitapnhom.courseweb.repository.CategoryRepository;
import com.baitapnhom.courseweb.repository.CourseRepository;
import com.baitapnhom.courseweb.repository.LessonRepository;
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

    @Autowired
    private LessonRepository lessonRepository;

    public CourseResponse createRequest(CourseRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("KHÔNG TÌM THẤY Category VỚI ID: " + request.getCategoryId()));

        Course course = new Course();
        course.setTeacherId(request.getTeacherId());
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPrice(request.getPrice());
        course.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
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
        return toResponse(findCourseById(id));
    }

    public String deleteCourse(String id) {
        courseRepository.deleteById(id);
        // xóa tất cả lesson trong course
        lessonRepository.deleteAllByCourse_Id(id);
        return "Course ĐÃ ĐƯỢC XOÁ THÀNH CÔNG!";
    }

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
        Course saved = courseRepository.save(course);
        return toResponse(saved);
    }

    // hàm nội bộ trả về Entity (dùng cho getCourseById và updateCourse)
    private Course findCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KHÔNG TÌM THẤY Course với ID: " + id));
    }

    // map Entity -> Response bằng tay (gán trực tiếp từng field)
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
        }
        return response;
    }
}