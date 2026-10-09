package com.baitapnhom.courseweb.repository;

import com.baitapnhom.courseweb.entity.Enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    
    boolean existsByStudentStudentIdAndCourseId(String studentId, String courseId);
    
    List<Enrollment> findByStudentStudentIdOrderByEnrollDateDesc(String studentId);
    
    Optional<Enrollment> findByStudentStudentIdAndCourseId(String studentId, String courseId);
    
    Page<Enrollment> findByStudentStudentId(String studentId, Pageable pageable);
    
    boolean existsByStudentStudentIdAndCourseIdAndStatus(String studentId, String courseId, com.baitapnhom.courseweb.enums.EnrollmentStatus status);
}
