package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.entity.Course;
import com.baitapnhom.courseweb.entity.Enrollment;
import com.baitapnhom.courseweb.entity.Student;

import com.baitapnhom.courseweb.enums.EnrollmentStatus;

import com.baitapnhom.courseweb.repository.CourseRepository;
import com.baitapnhom.courseweb.repository.EnrollmentRepository;
import com.baitapnhom.courseweb.repository.StudentRepository;

import com.baitapnhom.courseweb.dto.response.EnrollmentResponse;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {
    //  Khai báo các dependency với từ khóa final
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public EnrollmentResponse enrollCourse(String studentId, String courseId) {
        // 1. Kiểm tra student tồn tại
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));  // ← Sửa

        // 2. Kiểm tra course tồn tại
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));  // ← Sửa (thêm error code)

        // 3. Kiểm tra enrollment hiện tại
        Optional<Enrollment> optionalEnrollment = enrollmentRepository
            .findByStudentStudentIdAndCourseId(studentId, courseId);

        if (optionalEnrollment.isPresent()) {
            Enrollment existingEnrollment = optionalEnrollment.get();

            switch (existingEnrollment.getStatus()) {
                case ACTIVE -> throw new AppException(ErrorCode.ALREADY_ENROLLED);
                case COMPLETED -> throw new AppException(ErrorCode.COURSE_COMPLETED);
                case CANCELED -> {
                    existingEnrollment.setStatus(EnrollmentStatus.ACTIVE);
                    enrollmentRepository.saveAndFlush(existingEnrollment);
                    return new EnrollmentResponse(
                        existingEnrollment.getId(),
                        existingEnrollment.getCourse().getTitle(),
                        existingEnrollment.getStatus().name(),
                        existingEnrollment.getEnrollDate()
                    );
                }
            }
        }

        // 5b. Tạo enrollment mới
        Enrollment newEnrollment = new Enrollment(student, course, EnrollmentStatus.ACTIVE);
        newEnrollment = enrollmentRepository.saveAndFlush(newEnrollment);

        return new EnrollmentResponse(
            newEnrollment.getId(),
            newEnrollment.getCourse().getTitle(),
            newEnrollment.getStatus().name(),
            newEnrollment.getEnrollDate()
        );
    }

    public List<EnrollmentResponse> getMyCourses(String studentId) {
        // Lấy danh sách khóa học mà studentId này đã đăng ký
        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentIdOrderByEnrollDateDesc(studentId);
        // Chuyển đổi từ Entity (Enrollment) sang DTO (EnrollmentResponse) để trả về Controller
        return enrollments.stream().map(enrollment -> new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus().name(),
                enrollment.getEnrollDate()
        )).collect(Collectors.toList());
    }

    public boolean checkEnrollmentStatus(String studentId, String courseId) {
        // Gọi thẳng xuống DB để đếm xem có tồn tại bản ghi ghép cặp này chưa
        return enrollmentRepository.existsByStudentStudentIdAndCourseId(studentId, courseId);
    }
}