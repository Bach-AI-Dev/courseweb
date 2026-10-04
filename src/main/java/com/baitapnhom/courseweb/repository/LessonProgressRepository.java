package com.baitapnhom.courseweb.repository;

import com.baitapnhom.courseweb.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress,Integer>{
    Optional<LessonProgress> findByVideoLesson_LessonIdAndStudent_StudentId(String lessonId, String studentId);
    int countByStudentStudentIdAndVideoLessonLessonCourseIdAndIsCompletedTrue(String studentId, String courseId);


}

