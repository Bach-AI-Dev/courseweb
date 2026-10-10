package com.baitapnhom.courseweb.repository;

import com.baitapnhom.courseweb.entity.Enrollment;
import com.baitapnhom.courseweb.enums.EnrollmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    
    // Kiểm tra học viên (User) đã đăng ký khóa học chưa
    boolean existsByStudentIdAndCourseId(String studentId, String courseId);
    
    // Danh sách đăng ký của 1 học viên, sắp xếp mới nhất
    List<Enrollment> findByStudentIdOrderByEnrollDateDesc(String studentId);
    
    // Lấy thông tin đăng ký cụ thể
    Optional<Enrollment> findByStudentIdAndCourseId(String studentId, String courseId);
    
    // Phân trang danh sách đăng ký của học viên
    Page<Enrollment> findByStudentId(String studentId, Pageable pageable);
    List<Enrollment> findByCourseIdAndStatusNot(String courseId, EnrollmentStatus status);
    // Kiểm tra theo trạng thái đăng ký
    boolean existsByStudentIdAndCourseIdAndStatus(String studentId, String courseId, EnrollmentStatus status);
}