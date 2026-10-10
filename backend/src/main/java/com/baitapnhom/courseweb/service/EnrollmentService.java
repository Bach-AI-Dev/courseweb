package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.entity.Course;
import com.baitapnhom.courseweb.entity.Enrollment;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.enums.EnrollmentStatus;
import com.baitapnhom.courseweb.enums.CourseStatus;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.CourseRepository;
import com.baitapnhom.courseweb.repository.EnrollmentRepository;
import com.baitapnhom.courseweb.repository.UserRepository;
import com.baitapnhom.courseweb.dto.response.EnrollmentResponse;
import com.baitapnhom.courseweb.dto.response.PagedEnrollmentResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class xử lý logic đăng ký khóa học
 */
@Service
public class EnrollmentService {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);
    
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Đăng ký sinh viên vào khóa học
     * 
     * Logic:
     * 1. Kiểm tra sinh viên có tồn tại không
     * 2. Kiểm tra khóa học có tồn tại và ACTIVE không
     * 3. Kiểm tra enrollment hiện tại:
     *    - ACTIVE: Ném lỗi "đã đăng ký"
     *    - COMPLETED: Ném lỗi "đã hoàn thành"
     *    - CANCELED: Kích hoạt lại
     *    - Không tồn tại: Tạo mới
     */
    @Transactional
    public EnrollmentResponse enrollCourse(String studentId, String courseId) {
        logger.info("Student {} attempting to enroll in course {}", studentId, courseId);
        
        try {
            // Bước 1: Tìm người dùng theo ID (đóng vai trò là student)
            User student = userRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("User/Student not found: {}", studentId);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

            // Bước 2: Tìm khóa học
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.error("Course not found: {}", courseId);
                    return new AppException(ErrorCode.COURSE_NOT_FOUND);
                });
            
            // Bước 3: Kiểm tra khóa học có PUBLISHED không
            if (course.getStatus() == null || course.getStatus() != CourseStatus.PUBLISHED) {
                logger.warn("Course {} status is {}, cannot enroll", courseId, course.getStatus());
                throw new AppException(ErrorCode.COURSE_NOT_ACTIVE);
            }
            
            // Bước 4: Kiểm tra giá khóa học hợp lệ
            if (course.getPrice() == null || course.getPrice().signum() < 0) {
                logger.error("Invalid course price for course {}: {}", courseId, course.getPrice());
                throw new AppException(ErrorCode.INVALID_COURSE_PRICE);
            }

            // Bước 5: Xử lý enrollment hiện tại
            Optional<Enrollment> optionalEnrollment = enrollmentRepository
                .findByStudentIdAndCourseId(studentId, courseId);

            if (optionalEnrollment.isPresent()) {
                Enrollment existingEnrollment = optionalEnrollment.get();
                
                logger.info("Existing enrollment found with status: {}", existingEnrollment.getStatus());

                switch (existingEnrollment.getStatus()) {
                    case ACTIVE:
                        logger.warn("Student {} already enrolled in course {}", studentId, courseId);
                        throw new AppException(ErrorCode.ALREADY_ENROLLED);
                        
                    case COMPLETED:
                        logger.warn("Student {} already completed course {}", studentId, courseId);
                        throw new AppException(ErrorCode.COURSE_COMPLETED);
                        
                    case CANCELED:
                        // Kích hoạt lại enrollment đã hủy
                        existingEnrollment.setStatus(EnrollmentStatus.ACTIVE);
                        existingEnrollment.setEnrollDate(LocalDateTime.now());
                        enrollmentRepository.save(existingEnrollment);
                        logger.info("Reactivated enrollment for student {} in course {}", studentId, courseId);
                        return mapToResponse(existingEnrollment);
                }
            }

            // Bước 6: Tạo enrollment mới với User làm student
            Enrollment newEnrollment = new Enrollment(student, course, EnrollmentStatus.ACTIVE);
            newEnrollment = enrollmentRepository.save(newEnrollment);

            logger.info("Successfully enrolled student {} to course {}, enrollment ID: {}", 
                       studentId, courseId, newEnrollment.getId());
            return mapToResponse(newEnrollment);
            
        } catch (AppException e) {
            logger.error("Application error during enrollment - Student: {}, Course: {}, Error: {}", 
                        studentId, courseId, e.getErrorCode());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error...", e);
            throw new AppException(ErrorCode.UNCATEGORIED_EXISTED, e); 
        }
    }

    // Helper method: Chuyển đổi Enrollment entity thành EnrollmentResponse DTO

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
            enrollment.getId(),
            enrollment.getStudent().getId(),
            enrollment.getStudent().getFullName(),
            enrollment.getStudent().getEmail(),
            enrollment.getCourse().getId(),
            enrollment.getCourse().getTitle(),
            enrollment.getStatus().name(),
            enrollment.getEnrollDate()
        );
    }

    // Lấy danh sách tất cả khóa học của học viên (không phân trang)
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyCourses(String studentId) {
        logger.info("Fetching all courses for student {}", studentId);
        
        try {
            List<Enrollment> enrollments = enrollmentRepository
                .findByStudentIdOrderByEnrollDateDesc(studentId);
            
            logger.info("Found {} courses for student {}", enrollments.size(), studentId);
            
            return enrollments.stream()
                .map(this::mapToResponse)  // Dùng helper method
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Unexpected error...", e);
            throw new AppException(ErrorCode.UNCATEGORIED_EXISTED, e); 
        }
    }

    // Lấy danh sách khóa học của học viên (có phân trang)

    @Transactional(readOnly = true)
    public PagedEnrollmentResponse getMyCoursesPaginated(String studentId, int page, int size) {
        logger.info("Fetching paginated courses for student {} - page: {}, size: {}", 
                   studentId, page, size);
        
        try {
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 50) size = 50;

            // Tạo Pageable object với sort theo enrollDate giảm dần
            Pageable pageable = PageRequest.of(page, size, Sort.by("enrollDate").descending());
            
            // Lấy data từ DB
            Page<Enrollment> enrollmentPage = enrollmentRepository
                .findByStudentId(studentId, pageable);

            // Convert entities to DTOs
            List<EnrollmentResponse> responses = enrollmentPage.getContent().stream()
                .map(this::mapToResponse)  // Dùng helper method
                .collect(Collectors.toList());

            logger.info("Returned {} courses on page {} for student {}", 
                       responses.size(), page, studentId);

            // Return paginated response
            return new PagedEnrollmentResponse(
                responses,
                enrollmentPage.getTotalPages(),
                enrollmentPage.getTotalElements(),
                page,
                size
            );
            
        } catch (Exception e) {
            logger.error("Unexpected error...", e);
            throw new AppException(ErrorCode.UNCATEGORIED_EXISTED, e); 
        }
    }

    // Kiểm tra xem học viên đã đăng ký khóa học này chưa
    
    @Transactional(readOnly = true)
    public boolean checkEnrollmentStatus(String studentId, String courseId) {
        logger.debug("Checking enrollment status for student {} in course {}", studentId, courseId);
        return enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ACTIVE);
    }

    /**
     * Hủy đăng ký khóa học
     * Điều kiện:
     * - Không thể hủy khóa học đã hoàn thành (COMPLETED)
     * - Khóa học phải tồn tại
     */
    @Transactional
    public void cancelEnrollment(String studentId, String courseId) {
        logger.info("Student {} canceling enrollment in course {}", studentId, courseId);
        
        try {
            // Tìm enrollment
            Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> {
                    logger.error("Enrollment not found for student {} in course {}", studentId, courseId);
                    return new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
                });

            // Kiểm tra không được hủy khóa hoàn thành
            if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
                logger.warn("Cannot cancel completed enrollment: {}", enrollment.getId());
                throw new AppException(ErrorCode.CANNOT_CANCEL_COMPLETED);
            }

            // Cập nhật trạng thái thành CANCELED
            enrollment.setStatus(EnrollmentStatus.CANCELED);
            enrollmentRepository.save(enrollment);
            
            logger.info("Successfully canceled enrollment {} for student {}", enrollment.getId(), studentId);
            
        } catch (AppException e) {
            logger.error("Application error during cancellation: {}", e.getErrorCode());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error...", e);
            throw new AppException(ErrorCode.UNCATEGORIED_EXISTED, e); 
        }
    }

    // Lấy thông tin chi tiết enrollment theo ID
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(String enrollmentId, String studentId) {
        logger.info("Fetching enrollment {} for student {}", enrollmentId, studentId);
        
        try {
            // Tìm enrollment theo ID
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> {
                    logger.error("Enrollment not found: {}", enrollmentId);
                    return new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
                });

            // BẢO MẬT: Sinh viên chỉ có thể xem enrollment của chính họ (so sánh user id)
            if (!enrollment.getStudent().getId().equals(studentId)) {
                logger.warn("Unauthorized access to enrollment {} by student {}", enrollmentId, studentId);
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            logger.info("Successfully retrieved enrollment {}", enrollmentId);
            return mapToResponse(enrollment);
            
        } catch (AppException e) {
            logger.error("Application error: {}", e.getErrorCode());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error...", e);
            throw new AppException(ErrorCode.UNCATEGORIED_EXISTED, e); 
        }
    }
}