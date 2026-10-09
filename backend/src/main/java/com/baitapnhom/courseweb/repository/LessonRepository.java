package com.baitapnhom.courseweb.repository;

import com.baitapnhom.courseweb.entity.Lesson;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
    @Transactional
//    void deleteAllByCourse_Id(String courseId);
    // Tìm tất cả bài học của một khóa học, sắp xếp theo thứ tự (lessonOrder) Tăng dần (Ascending)
    List<Lesson> findByCourseIdOrderByLessonOrderAsc(String courseId);
    // Đếm xem khóa học này có tổng cộng bao nhiêu bài
    int countByCourseId(String courseId);
}
