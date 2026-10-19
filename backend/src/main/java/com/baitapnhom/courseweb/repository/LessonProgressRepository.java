package com.baitapnhom.courseweb.repository;

import com.baitapnhom.courseweb.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, String> {

    // Tìm tiến độ của học viên cho 1 bài học
    Optional<LessonProgress> findByLessonIdAndStudentId(String lessonId, String studentId);

    // Đếm số bài học đã hoàn thành trong 1 khóa học của 1 học viên
    int countByStudentIdAndLessonCourseIdAndIsCompletedTrue(String studentId, String courseId);
}
