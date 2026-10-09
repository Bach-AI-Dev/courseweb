package com.baitapnhom.courseweb.repository;

import com.baitapnhom.courseweb.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    // Đếm xem khóa học này có bao nhiêu bài học (lesson)
    @Query(value = "SELECT COUNT(*) FROM lessons WHERE course_id = :courseId", nativeQuery = true)
    long countLessonsByCourseId(@Param("courseId") String courseId);

    // Đếm xem khóa học này có bao nhiêu người đăng ký (enrollment)
    @Query(value = "SELECT COUNT(*) FROM enrollments WHERE course_id = :courseId", nativeQuery = true)
    long countEnrollmentsByCourseId(@Param("courseId") String courseId);
}