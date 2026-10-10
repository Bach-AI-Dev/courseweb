package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.LessonProgressRequest;
import com.baitapnhom.courseweb.dto.response.CourseMemberProgressResponse;
import com.baitapnhom.courseweb.dto.response.CourseProgressResponse;
import com.baitapnhom.courseweb.dto.response.LessonProgressResponse;

import java.util.List;


public interface ILessonProgressService {
    LessonProgressResponse updateProgress(LessonProgressRequest request,String studentId);

    CourseProgressResponse getCourseProgress(String courseId, String studentId);

    LessonProgressResponse getLessonProgress(String lessonId, String studentId);

    List<CourseMemberProgressResponse> getCourseMembersProgress(String courseId);
}

