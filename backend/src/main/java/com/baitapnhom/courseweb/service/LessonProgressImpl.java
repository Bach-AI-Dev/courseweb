package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.LessonProgressRequest;
import com.baitapnhom.courseweb.dto.response.CourseProgressResponse;
import com.baitapnhom.courseweb.dto.response.LessonProgressResponse;
import org.springframework.stereotype.Service;

@Service
public class LessonProgressImpl implements ILessonProgressService {

    @Override
    public LessonProgressResponse updateProgress(LessonProgressRequest request) {
        return null;
    }

    @Override
    public CourseProgressResponse getCourseProgress(String courseId, String studentId) {
        return null;
    }

    @Override
    public LessonProgressResponse getLessonProgress(String lessonId, String studentId) {
        return null;
    }
}