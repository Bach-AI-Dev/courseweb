package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.response.CourseEnrollmentSummaryResponse;
import com.baitapnhom.courseweb.dto.response.EnrollmentResponse;
import com.baitapnhom.courseweb.dto.response.PagedEnrollmentResponse;
import com.baitapnhom.courseweb.entity.Course;
import com.baitapnhom.courseweb.entity.Enrollment;
import com.baitapnhom.courseweb.entity.LessonProgress;
import com.baitapnhom.courseweb.entity.User;
import com.baitapnhom.courseweb.enums.CourseStatus;
import com.baitapnhom.courseweb.enums.EnrollmentStatus;
import com.baitapnhom.courseweb.enums.Role;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.CourseRepository;
import com.baitapnhom.courseweb.repository.EnrollmentRepository;
import com.baitapnhom.courseweb.repository.LessonProgressRepository;
import com.baitapnhom.courseweb.repository.LessonRepository;
import com.baitapnhom.courseweb.repository.UserRepository;
import com.baitapnhom.courseweb.repository.VideoLessonsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final VideoLessonsRepository videoLessonsRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository,
                             LessonRepository lessonRepository,
                             LessonProgressRepository lessonProgressRepository,
                             VideoLessonsRepository videoLessonsRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.videoLessonsRepository = videoLessonsRepository;
    }

    // =========================================================================
    // [DẠNG 11]: Lấy User đang đăng nhập trực tiếp tại tầng Service
    // Giải thích: Xóa bỏ UserRepository ở Controller, chỉ query bảng users đúng 1 lần duy nhất
    // =========================================================================
    @Transactional(readOnly = true)
    public User getCurrentLoggedInUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    // =========================================================================
    // CÁC HÀM PHỤ TRỢ BẰNG JAVA THUẦN (THAY THẾ HOÀN TOÀN VIỆC VIẾT SQL TRONG REPO)
    // =========================================================================

    // Đếm tổng số bài học của khóa học (Dùng cho Dạng 4, Dạng 18)
    private long countTotalLessonsInCourse(String courseId) {
        return lessonRepository.findAll().stream()
                .filter(lesson -> lesson.getCourse() != null && lesson.getCourse().getId().equals(courseId))
                .count();
    }

    // Đếm số bài học đã hoàn thành (is_completed = true) của học viên trong khóa học (Dùng cho Dạng 4, 5, 16, 18)
    private long countCompletedLessons(String studentId, String courseId) {
        return lessonProgressRepository.findAll().stream()
                .filter(lp -> lp.getStudent() != null && lp.getStudent().getId().equals(studentId)
                        && lp.getLesson() != null && lp.getLesson().getCourse() != null
                        && lp.getLesson().getCourse().getId().equals(courseId)
                        && Boolean.TRUE.equals(lp.getIsCompleted()))
                .count();
    }

    // Tính tổng thời gian đã xem video (watched_time_seconds) của học viên trong khóa học (Dùng cho Dạng 18, 20)
    private long sumWatchedSeconds(String studentId, String courseId) {
        return lessonProgressRepository.findAll().stream()
                .filter(lp -> lp.getStudent() != null && lp.getStudent().getId().equals(studentId)
                        && lp.getLesson() != null && lp.getLesson().getCourse() != null
                        && lp.getLesson().getCourse().getId().equals(courseId)
                        && lp.getWatchedTimeSeconds() != null)
                .mapToLong(LessonProgress::getWatchedTimeSeconds)
                .sum();
    }

    // [DẠNG 6]: Reset toàn bộ tiến độ học tập về 0 bằng vòng lặp for + saveAll (Không cần SQL UPDATE)
    private void resetStudentCourseProgress(String studentId, String courseId) {
        List<LessonProgress> allProgress = lessonProgressRepository.findAll();
        List<LessonProgress> toReset = new ArrayList<>();

        for (LessonProgress lp : allProgress) {
            if (lp.getStudent() != null && lp.getStudent().getId().equals(studentId)
                    && lp.getLesson() != null && lp.getLesson().getCourse() != null
                    && lp.getLesson().getCourse().getId().equals(courseId)) {
                lp.setWatchedTimeSeconds(0);
                lp.setIsCompleted(false);
                lp.setLastPosition(0);
                toReset.add(lp);
            }
        }
        if (!toReset.isEmpty()) {
            lessonProgressRepository.saveAll(toReset);
        }
    }

    // =========================================================================
    // [DẠNG 1, 3, 6, 8, 11, 14, 15]: Đăng ký khóa học (enrollCourse)
    // =========================================================================
    @Transactional
    public EnrollmentResponse enrollCourse(String courseId) {
        // Bước 1 [DẠNG 11]: Lấy trực tiếp User đang đăng nhập (chỉ tốn 1 câu query)
        User student = getCurrentLoggedInUser();
        String studentId = student.getId();
        logger.info("Student {} attempting to enroll in course {}", studentId, courseId);

        // [DẠNG 1 - Ý 2]: Nếu người dùng có Role là ADMIN thì chặn ngay ở tầng Service
        if (student.getRole() == Role.ADMIN) {
            throw new AppException(ErrorCode.ADMIN_CANNOT_ENROLL);
        }

        // Bước 2: Tìm khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // [DẠNG 1 - Ý 1]: Chặn Giảng viên tự đăng ký khóa học của chính mình (Self-Enrollment)
        if (course.getTeacher() != null && course.getTeacher().getId().equals(studentId)) {
            throw new AppException(ErrorCode.CANNOT_ENROLL_OWN_COURSE);
        }

        // Bước 3: Kiểm tra khóa học có PUBLISHED không
        if (course.getStatus() == null || course.getStatus() != CourseStatus.PUBLISHED) {
            throw new AppException(ErrorCode.COURSE_NOT_ACTIVE);
        }

        // Bước 4: Kiểm tra giá khóa học hợp lệ
        if (course.getPrice() == null || course.getPrice().signum() < 0) {
            throw new AppException(ErrorCode.INVALID_COURSE_PRICE);
        }

        Optional<Enrollment> optionalEnrollment = enrollmentRepository
                .findByStudentIdAndCourseId(studentId, courseId);

        if (optionalEnrollment.isPresent()) {
            Enrollment existingEnrollment = optionalEnrollment.get();
            switch (existingEnrollment.getStatus()) {
                case ACTIVE:
                    throw new AppException(ErrorCode.ALREADY_ENROLLED);
                case COMPLETED:
                    throw new AppException(ErrorCode.COURSE_COMPLETED);
                case CANCELED:
                    if (Duration.between(existingEnrollment.getEnrollDate(), LocalDateTime.now()).toHours() < 24) {
                        throw new AppException(ErrorCode.RE_ENROLL_COOLDOWN_ACTIVE);
                    }
                    break;
            }
        }

        
        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentIdOrderByEnrollDateDesc(studentId);

        long studentActiveCount = studentEnrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE)
                .count();
        if (studentActiveCount >= 1) {
            throw new AppException(ErrorCode.MAX_ACTIVE_COURSES_EXCEEDED);
        }

        long courseActiveCount = enrollmentRepository.findByCourseIdOrderByEnrollDateDesc(courseId).stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE)
                .count();
        if (courseActiveCount >= 30) {
            throw new AppException(ErrorCode.COURSE_CAPACITY_EXCEEDED);
        }

        if (course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            long activePaidCourses = studentEnrollments.stream()
                    .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE
                            && e.getCourse().getPrice() != null
                            && e.getCourse().getPrice().compareTo(BigDecimal.ZERO) > 0)
                    .count();
            if (activePaidCourses >= 2) {
                throw new AppException(ErrorCode.PAID_COURSE_LIMIT_EXCEEDED);
            }
        }

        try {
            if (optionalEnrollment.isPresent()) {
                Enrollment existingEnrollment = optionalEnrollment.get();

                // [DẠNG 6]: Reset toàn bộ tiến độ cũ trong bảng lesson_progress khi đăng ký lại từ CANCELED
                resetStudentCourseProgress(studentId, courseId);

                existingEnrollment.setStatus(EnrollmentStatus.ACTIVE);
                existingEnrollment.setEnrollDate(LocalDateTime.now());
                Enrollment saved = enrollmentRepository.saveAndFlush(existingEnrollment);
                return mapToResponse(saved);
            }

            Enrollment newEnrollment = new Enrollment(student, course, EnrollmentStatus.ACTIVE);
            newEnrollment = enrollmentRepository.saveAndFlush(newEnrollment);
            return mapToResponse(newEnrollment);

        } catch (DataIntegrityViolationException ex) {
            // [DẠNG 8]: Nếu 2 luồng song song cùng INSERT gây đụng độ UniqueConstraint -> trả về ALREADY_ENROLLED
            throw new AppException(ErrorCode.ALREADY_ENROLLED);
        }
    }

    // =========================================================================
    // [DẠNG 7]: Tách hàm mapToStudentResponse dùng Constructor 5 tham số
    // Giải thích: Không gọi enrollment.getStudent() để tránh kích hoạt truy vấn thừa xuống bảng users
    // =========================================================================
    private EnrollmentResponse mapToStudentResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus().name(),
                enrollment.getEnrollDate()
        );
    }

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

    // =========================================================================
    // [DẠNG 7 & DẠNG 11]: Lấy danh sách khóa học của học viên (getMyCourses)
    // =========================================================================
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyCourses() {
        String studentId = getCurrentLoggedInUser().getId();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdOrderByEnrollDateDesc(studentId);

        return enrollments.stream()
                .filter(e -> e.getStatus() != EnrollmentStatus.CANCELED)
                .map(this::mapToStudentResponse) // [DẠNG 7]: Dùng Constructor 5 tham số
                .collect(Collectors.toList());
    }

    // =========================================================================
    // [DẠNG 7, 11, 13]: Lấy danh sách khóa học có phân trang + lọc status (Không cần viết SQL mới)
    // =========================================================================
    @Transactional(readOnly = true)
    public PagedEnrollmentResponse getMyCoursesPaginated(EnrollmentStatus status, int page, int size) {
        // [DẠNG 13 - Ý 1]: Kiểm tra học viên tồn tại thông qua getCurrentLoggedInUser()
        String studentId = getCurrentLoggedInUser().getId();

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 50) size = 50;

        // [DẠNG 13 - Ý 3]: Dùng hàm có sẵn lấy danh sách và lọc theo status bằng Java Stream
        List<EnrollmentResponse> filteredList = enrollmentRepository.findByStudentIdOrderByEnrollDateDesc(studentId)
                .stream()
                .filter(e -> {
                    if (status == null) {
                        return e.getStatus() != EnrollmentStatus.CANCELED; // Mặc định lấy tất cả trừ CANCELED
                    }
                    return e.getStatus() == status;
                })
                .map(this::mapToStudentResponse) // [DẠNG 7]: Dùng Constructor 5 tham số
                .collect(Collectors.toList());

        long totalElements = filteredList.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // [DẠNG 13 - Ý 2]: Nếu truyền page >= totalPages (khi có dữ liệu) thì chặn lại ngay
        if (totalPages > 0 && page >= totalPages) {
            throw new AppException(ErrorCode.PAGE_OUT_OF_BOUNDS);
        }

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, filteredList.size());
        List<EnrollmentResponse> pageContent = (fromIndex >= filteredList.size())
                ? Collections.emptyList()
                : filteredList.subList(fromIndex, toIndex);

        return new PagedEnrollmentResponse(pageContent, totalPages, totalElements, page, size);
    }

    @Transactional(readOnly = true)
    public boolean checkEnrollmentStatus(String courseId) {
        String studentId = getCurrentLoggedInUser().getId();
        return enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ACTIVE);
    }

    // =========================================================================
    // [DẠNG 5, 6, 9, 11, 14]: Hủy đăng ký khóa học (cancelEnrollment)
    // =========================================================================
    @Transactional
    public void cancelEnrollment(String courseId) {
        String studentId = getCurrentLoggedInUser().getId();

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // [DẠNG 9]: Kiểm tra nếu khóa học đã bị đóng và lưu trữ (ARCHIVED) thì chặn hủy
        if (enrollment.getCourse().getStatus() == CourseStatus.ARCHIVED) {
            throw new AppException(ErrorCode.COURSE_ALREADY_ARCHIVED);
        }

        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_COMPLETED);
        }

        // [DẠNG 5 - Ý 1]: Chỉ cho phép hủy trong vòng 7 ngày kể từ enrollDate
        if (LocalDateTime.now().isAfter(enrollment.getEnrollDate().plusDays(7))) {
            throw new AppException(ErrorCode.CANCEL_DEADLINE_EXPIRED);
        }

        // [DẠNG 5 - Ý 2]: Nếu đã hoàn thành bất kỳ bài học nào (is_completed = 1) thì không cho tự hủy
        if (countCompletedLessons(studentId, courseId) > 0) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_STARTED_COURSE);
        }

        // [DẠNG 6]: Reset toàn bộ tiến độ học tập về 0 khi hủy
        resetStudentCourseProgress(studentId, courseId);

        enrollment.setStatus(EnrollmentStatus.CANCELED);
        // [DẠNG 14]: Lưu lại thời điểm hủy vào enrollDate để tính Cooldown 24h nếu đăng ký lại
        enrollment.setEnrollDate(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    // =========================================================================
    // [DẠNG 11 & DẠNG 12]: Lấy chi tiết phiếu đăng ký (getEnrollmentById)
    // =========================================================================
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(String enrollmentId) {
        User currentUser = getCurrentLoggedInUser();

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // [DẠNG 12 - Ý 1]: Cho phép nếu là chính học viên đó, HOẶC là ADMIN, HOẶC là TEACHER dạy khóa đó
        boolean isOwnerStudent = enrollment.getStudent().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isCourseTeacher = currentUser.getRole() == Role.TEACHER
                && enrollment.getCourse().getTeacher() != null
                && enrollment.getCourse().getTeacher().getId().equals(currentUser.getId());

        if (!isOwnerStudent && !isAdmin && !isCourseTeacher) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // [DẠNG 12 - Ý 2]: Nếu người gọi là STUDENT mà bản ghi đang CANCELED thì chặn lại
        if (currentUser.getRole() == Role.STUDENT && enrollment.getStatus() == EnrollmentStatus.CANCELED) {
            throw new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        return mapToResponse(enrollment);
    }

    // =========================================================================
    // [DẠNG 2 & DẠNG 11]: Xem danh sách học viên của khóa học (getEnrollmentsByCourseId)
    // =========================================================================
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourseId(String courseId) {
        User currentUser = getCurrentLoggedInUser();

        // [DẠNG 2 - Ý 1]: Kiểm tra courseId có tồn tại trong bảng courses không
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // [DẠNG 2 - Ý 2]: Chặn IDOR - Nếu là TEACHER nhưng không phải chủ khóa học đó thì ném UNAUTHORIZED
        if (currentUser.getRole() == Role.TEACHER &&
                (course.getTeacher() == null || !course.getTeacher().getId().equals(currentUser.getId()))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdOrderByEnrollDateDesc(courseId);
        return enrollments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // =========================================================================
    // [DẠNG 2, 4, 9, 11]: Cập nhật trạng thái học viên (updateEnrollmentStatus)
    // =========================================================================
    @Transactional
    public EnrollmentResponse updateEnrollmentStatus(String enrollmentId, EnrollmentStatus status) {
        User currentUser = getCurrentLoggedInUser();

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        Course course = enrollment.getCourse();

        // [DẠNG 2]: Chặn IDOR - Giảng viên A không được sửa trạng thái học viên của Giảng viên B
        if (currentUser.getRole() == Role.TEACHER &&
                (course.getTeacher() == null || !course.getTeacher().getId().equals(currentUser.getId()))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // [DẠNG 9]: Chặn cập nhật nếu khóa học đang ở trạng thái ARCHIVED
        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new AppException(ErrorCode.COURSE_ALREADY_ARCHIVED);
        }

        // [DẠNG 4]: Ràng buộc điều kiện COMPLETED với bảng lessons và lesson_progress
        if (status == EnrollmentStatus.COMPLETED) {
            String courseId = course.getId();
            String studentId = enrollment.getStudent().getId();

            long totalLessons = countTotalLessonsInCourse(courseId);
            long completedLessons = countCompletedLessons(studentId, courseId);

            if (totalLessons == 0 || completedLessons < totalLessons) {
                throw new AppException(ErrorCode.COURSE_NOT_COMPLETED_YET);
            }
        }

        enrollment.setStatus(status);
        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToResponse(saved);
    }

    // =========================================================================
    // [DẠNG 6]: Xóa đăng ký (Admin) đồng thời reset tiến độ học tập
    // =========================================================================
    @Transactional
    public void deleteEnrollmentById(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        resetStudentCourseProgress(enrollment.getStudent().getId(), enrollment.getCourse().getId());
        enrollmentRepository.delete(enrollment);
    }

    // =========================================================================
    // [DẠNG 10]: Thống kê khóa học bằng Stream API (getCourseEnrollmentSummary)
    // =========================================================================
    @Transactional(readOnly = true)
    public CourseEnrollmentSummaryResponse getCourseEnrollmentSummary(String courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        // Dùng hàm có sẵn lấy danh sách và đếm bằng Stream API
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdOrderByEnrollDateDesc(courseId);

        long activeCount = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE).count();
        long completedCount = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
        long canceledCount = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.CANCELED).count();

        long totalValid = activeCount + completedCount;
        double completionRate = 0.0;
        if (totalValid > 0) {
            completionRate = BigDecimal.valueOf((completedCount * 100.0) / totalValid)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new CourseEnrollmentSummaryResponse(courseId, activeCount, completedCount, canceledCount, completionRate);
    }

    // =========================================================================
    // [DẠNG 16 - Thêm mới 1]: Nghiệp vụ Chuyển đổi khóa học (transferCourse)
    // =========================================================================
    @Transactional
    public EnrollmentResponse transferCourse(String studentId, String fromCourseId, String toCourseId) {
        // 1. Kiểm tra fromCourseId và toCourseId không được trùng nhau
        if (fromCourseId == null || toCourseId == null || fromCourseId.equals(toCourseId)) {
            throw new AppException(ErrorCode.INVALID_COURSE_TRANSFER);
        }

        // 2. Học viên phải đang có bản ghi ACTIVE tại khóa fromCourseId
        Enrollment fromEnrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, fromCourseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (fromEnrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new AppException(ErrorCode.INVALID_COURSE_TRANSFER);
        }

        // 3. Học viên chưa từng hoàn thành bất kỳ bài học nào (is_completed = 1) của khóa fromCourseId
        if (countCompletedLessons(studentId, fromCourseId) > 0) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_STARTED_COURSE);
        }

        // 4. Khóa đích phải tồn tại, PUBLISHED và có price <= khóa cũ
        Course fromCourse = fromEnrollment.getCourse();
        Course toCourse = courseRepository.findById(toCourseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (toCourse.getStatus() != CourseStatus.PUBLISHED) {
            throw new AppException(ErrorCode.COURSE_NOT_ACTIVE);
        }
        if (toCourse.getPrice().compareTo(fromCourse.getPrice()) > 0) {
            throw new AppException(ErrorCode.TRANSFER_PRICE_EXCEEDED);
        }

        // 5. Hủy bản ghi khóa cũ, tạo mới hoặc kích hoạt lại bản ghi khóa mới
        resetStudentCourseProgress(studentId, fromCourseId);
        fromEnrollment.setStatus(EnrollmentStatus.CANCELED);
        enrollmentRepository.save(fromEnrollment);

        Optional<Enrollment> existingTarget = enrollmentRepository.findByStudentIdAndCourseId(studentId, toCourseId);
        Enrollment targetEnrollment;
        if (existingTarget.isPresent()) {
            targetEnrollment = existingTarget.get();
            if (targetEnrollment.getStatus() == EnrollmentStatus.ACTIVE || targetEnrollment.getStatus() == EnrollmentStatus.COMPLETED) {
                throw new AppException(ErrorCode.ALREADY_ENROLLED);
            }
            resetStudentCourseProgress(studentId, toCourseId);
            targetEnrollment.setStatus(EnrollmentStatus.ACTIVE);
            targetEnrollment.setEnrollDate(LocalDateTime.now());
        } else {
            targetEnrollment = new Enrollment(fromEnrollment.getStudent(), toCourse, EnrollmentStatus.ACTIVE);
        }

        return mapToResponse(enrollmentRepository.save(targetEnrollment));
    }

    // =========================================================================
    // [DẠNG 17 - Thêm mới 2]: Ghi danh hàng loạt cho Admin/Teacher (enrollBatchStudents)
    // =========================================================================
    @Transactional
    public List<EnrollmentResponse> enrollBatchStudents(String courseId, List<String> studentIds) {
        // 1. Kiểm tra khóa học tồn tại và đang PUBLISHED
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new AppException(ErrorCode.COURSE_NOT_ACTIVE);
        }

        if (studentIds == null || studentIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Dùng .distinct() để lọc trùng danh sách studentIds
        List<String> distinctIds = studentIds.stream()
                .filter(id -> id != null && !id.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        List<Enrollment> toSave = new ArrayList<>();

        // 3. Duyệt qua từng sinh viên
        for (String studentId : distinctIds) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Optional<Enrollment> existingOpt = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
            if (existingOpt.isPresent()) {
                Enrollment existing = existingOpt.get();
                // Nếu đã ACTIVE hoặc COMPLETED -> bỏ qua (skip)
                if (existing.getStatus() == EnrollmentStatus.ACTIVE || existing.getStatus() == EnrollmentStatus.COMPLETED) {
                    continue;
                }
                // Nếu đang CANCELED -> kích hoạt lại thành ACTIVE
                existing.setStatus(EnrollmentStatus.ACTIVE);
                existing.setEnrollDate(LocalDateTime.now());
                toSave.add(existing);
            } else {
                toSave.add(new Enrollment(student, course, EnrollmentStatus.ACTIVE));
            }
        }

        // 4. Lưu toàn bộ bằng saveAll
        return enrollmentRepository.saveAll(toSave).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // [DẠNG 18 - Thêm mới 3]: Tự động xét duyệt Hoàn thành khóa học (evaluateAndCompleteEnrollment)
    // =========================================================================
    @Transactional
    public EnrollmentResponse evaluateAndCompleteEnrollment(String studentId, String courseId) {
        // 1. Kiểm tra học viên phải đang ACTIVE trong khóa học
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new AppException(ErrorCode.INSUFFICIENT_LEARNING_PROGRESS);
        }

        // 2. Nếu khóa học có 0 bài học thì chặn lại
        long totalLessons = countTotalLessonsInCourse(courseId);
        if (totalLessons == 0) {
            throw new AppException(ErrorCode.COURSE_NOT_COMPLETED_YET);
        }

        // 3. Kiểm tra số bài is_completed = 1 phải bằng đúng tổng số bài học
        long completedLessons = countCompletedLessons(studentId, courseId);
        if (completedLessons < totalLessons) {
            throw new AppException(ErrorCode.INSUFFICIENT_LEARNING_PROGRESS);
        }

        // 3 (Nâng cao): Tổng watched_time_seconds phải đạt tối thiểu 80% tổng duration_seconds trong video_lessons
        long totalCourseVideoDuration = videoLessonsRepository.findAll().stream()
                .filter(v -> v.getLesson() != null && v.getLesson().getCourse() != null
                        && v.getLesson().getCourse().getId().equals(courseId)
                        && v.getDurationSeconds() != null)
                .mapToLong(v -> v.getDurationSeconds())
                .sum();

        long totalWatchedSeconds = sumWatchedSeconds(studentId, courseId);
        if (totalCourseVideoDuration > 0 && totalWatchedSeconds < (totalCourseVideoDuration * 0.8)) {
            throw new AppException(ErrorCode.INSUFFICIENT_LEARNING_PROGRESS);
        }

        // 4. Đạt đủ điều kiện -> chuyển sang COMPLETED
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        return mapToResponse(enrollmentRepository.save(enrollment));
    }

    // =========================================================================
    // [DẠNG 19 - Thêm mới 4]: Tìm kiếm & Lọc học viên đa tiêu chí có Phân trang (Bằng Java Stream, 0% SQL)
    // =========================================================================
    @Transactional(readOnly = true)
    public PagedEnrollmentResponse searchCourseEnrollments(
            String courseId,
            EnrollmentStatus status,
            String keyword,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size) {

        // 1. Kiểm tra fromDate và toDate
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 50) size = 50;

        // 2. Lấy danh sách bằng hàm có sẵn và lọc linh hoạt bằng Stream API
        List<EnrollmentResponse> filteredList = enrollmentRepository.findByCourseIdOrderByEnrollDateDesc(courseId)
                .stream()
                .filter(e -> status == null || e.getStatus() == status)
                .filter(e -> {
                    if (keyword == null || keyword.trim().isEmpty()) return true;
                    String kw = keyword.trim().toLowerCase();
                    String fullName = e.getStudent().getFullName() != null ? e.getStudent().getFullName().toLowerCase() : "";
                    String email = e.getStudent().getEmail() != null ? e.getStudent().getEmail().toLowerCase() : "";
                    return fullName.contains(kw) || email.contains(kw);
                })
                .filter(e -> fromDate == null || !e.getEnrollDate().isBefore(fromDate))
                .filter(e -> toDate == null || !e.getEnrollDate().isAfter(toDate))
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // 3. Phân trang ngay trên danh sách đã lọc
        long totalElements = filteredList.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, filteredList.size());

        List<EnrollmentResponse> pageContent = (fromIndex >= filteredList.size())
                ? Collections.emptyList()
                : filteredList.subList(fromIndex, toIndex);

        return new PagedEnrollmentResponse(pageContent, totalPages, totalElements, page, size);
    }

    // =========================================================================
    // [DẠNG 20 - Thêm mới 5]: Thu hồi tự động các tài khoản "đăng ký ảo" không học (0% SQL)
    // =========================================================================
    @Transactional
    public int revokeInactiveEnrollments(String courseId, int maxInactiveDays) {
        // 1. Kiểm tra maxInactiveDays phải > 0
        if (maxInactiveDays <= 0) {
            throw new AppException(ErrorCode.INVALID_INACTIVE_DAYS);
        }

        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(maxInactiveDays);
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdOrderByEnrollDateDesc(courseId);
        List<Enrollment> revokedList = new ArrayList<>();

        // 2 & 3. Lọc các bản ghi ACTIVE trước cutoffDate mà tổng thời gian học == 0
        for (Enrollment e : enrollments) {
            if (e.getStatus() == EnrollmentStatus.ACTIVE && e.getEnrollDate().isBefore(cutoffDate)) {
                long watchedSeconds = sumWatchedSeconds(e.getStudent().getId(), courseId);
                if (watchedSeconds == 0) {
                    e.setStatus(EnrollmentStatus.CANCELED);
                    revokedList.add(e);
                }
            }
        }

        // 4. Lưu lại và trả về số lượng học viên bị thu hồi
        if (!revokedList.isEmpty()) {
            enrollmentRepository.saveAll(revokedList);
        }
        return revokedList.size();
    }
}