package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.LessonProgressRequest;
import com.baitapnhom.courseweb.dto.response.CourseProgressResponse;
import com.baitapnhom.courseweb.dto.response.LessonProgressResponse;


public interface ILessonProgressService {
    LessonProgressResponse updateProgress(LessonProgressRequest request);

    CourseProgressResponse getCourseProgress(String courseId, String studentId);

    LessonProgressResponse getLessonProgress(String lessonId, String studentId);
}

