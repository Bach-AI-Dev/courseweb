package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.LessonProgressRequest;
import com.baitapnhom.courseweb.dto.response.CourseMemberProgressResponse;
import com.baitapnhom.courseweb.dto.response.CourseProgressResponse;
import com.baitapnhom.courseweb.dto.response.LessonProgressResponse;
import com.baitapnhom.courseweb.entity.*;
import com.baitapnhom.courseweb.enums.EnrollmentStatus;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // THÊM DÒNG NÀY ĐỂ IMPORT THỜI GIAN
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LessonProgressImpl implements ILessonProgressService {

    private final LessonProgressRepository progressRepository;
    private final VideoLessonsRepository videoRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;

    public LessonProgressImpl(LessonProgressRepository progressRepository,
                              VideoLessonsRepository videoRepository,
                              UserRepository userRepository,
                              EnrollmentRepository enrollmentRepository, LessonRepository lessonRepository) {
        this.progressRepository = progressRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public LessonProgressResponse updateProgress(LessonProgressRequest request,String studentId) {

        Optional<VideoLessons> videoRequest = videoRepository.findById(request.getLessonId());

        if (videoRequest.isEmpty()) {
            throw new AppException(ErrorCode.VIDEO_NOT_FOUND);
        }

        VideoLessons video = videoRequest.get();

        String courseId = video.getLesson().getCourse().getId();

        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
        if (!isEnrolled) {
            throw new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        int durationVideo = video.getDurationSeconds();
        if (durationVideo <= 0) {
            durationVideo = 1;
        }

        Optional<LessonProgress> progressRequest = progressRepository.findByLessonIdAndStudentId(request.getLessonId(), studentId);

        int tolerance = 2;
        LessonProgress savedProgress;

        int validTimeDelta = Math.max(request.getTimeDelta(), 0);

        if (progressRequest.isPresent()) {
            LessonProgress progress = progressRequest.get();

            int newWatchedTimeSeconds = progress.getWatchedTimeSeconds() + validTimeDelta;
            newWatchedTimeSeconds = Math.min(newWatchedTimeSeconds, durationVideo);

            int newLastPosition = Math.min(request.getLastPosition(), durationVideo);

            if (newWatchedTimeSeconds >= (durationVideo - tolerance) && !progress.getIsCompleted() && newLastPosition >= (durationVideo - tolerance)) {
                progress.setIsCompleted(true);
            }

            progress.setWatchedTimeSeconds(newWatchedTimeSeconds);
            progress.setLastPosition(newLastPosition);

            // THÊM DÒNG NÀY: Cập nhật thời gian xem lần cuối (cho tiến độ cũ)
            progress.setLastWatchedAt(LocalDateTime.now());

            savedProgress = progressRepository.save(progress);
        }
        else {
            LessonProgress newProgress = new LessonProgress();

            newProgress.setLesson(video.getLesson());
            newProgress.setStudent(userRepository.getReferenceById(studentId));

            int initalTime = Math.min(validTimeDelta, durationVideo);
            newProgress.setWatchedTimeSeconds(initalTime);

            int newLastPosition = Math.min(request.getLastPosition(), durationVideo);
            newProgress.setLastPosition(newLastPosition);

            if (initalTime >= (durationVideo - tolerance) && newLastPosition >= (durationVideo - tolerance)) {
                newProgress.setIsCompleted(true);
            }
            else {
                newProgress.setIsCompleted(false);
            }

            // THÊM DÒNG NÀY: Cập nhật thời gian xem lần cuối (cho tiến độ mới tạo)
            newProgress.setLastWatchedAt(LocalDateTime.now());

            savedProgress = progressRepository.save(newProgress);
        }

        double percentage = (double) savedProgress.getWatchedTimeSeconds() / durationVideo * 100;
        int percentCompleted = (int) Math.round(percentage);

        LessonProgressResponse responseDto = new LessonProgressResponse();
        responseDto.setStudentId(studentId);
        responseDto.setLessonId(request.getLessonId());
        responseDto.setWatchedTimeSeconds(savedProgress.getWatchedTimeSeconds());
        responseDto.setIsCompleted(savedProgress.getIsCompleted());
        responseDto.setCompletionPercentage(percentCompleted);
        responseDto.setLastPosition(savedProgress.getLastPosition());

        return responseDto;
    }

    @Override
    public CourseProgressResponse getCourseProgress(String courseId, String studentId) {
        int totalLessons = lessonRepository.countByCourseId(courseId);

        if (totalLessons == 0) {
            CourseProgressResponse emptyResponse = new CourseProgressResponse();
            emptyResponse.setCourseId(courseId);
            emptyResponse.setStudentId(studentId);
            emptyResponse.setTotalLessons(0);
            emptyResponse.setCompletedLessons(0);
            emptyResponse.setProgressPercentage(0);
            return emptyResponse;
        }

        int completedLessons = progressRepository.countByStudentIdAndLessonCourseIdAndIsCompletedTrue(studentId, courseId);

        double percentage = (double) completedLessons / totalLessons * 100;
        int progressPercentage = (int) Math.round(percentage);

        CourseProgressResponse response = new CourseProgressResponse();
        response.setCourseId(courseId);
        response.setStudentId(studentId);
        response.setTotalLessons(totalLessons);
        response.setCompletedLessons(completedLessons);
        response.setProgressPercentage(progressPercentage);

        return response;
    }

    @Override
    public LessonProgressResponse getLessonProgress(String lessonId, String studentId) {

        LessonProgressResponse response = new LessonProgressResponse();
        response.setLessonId(lessonId);
        response.setStudentId(studentId);

        // 1. Lấy thông tin video giữ nguyên trong hộp Optional
        Optional<VideoLessons> videoOpt = videoRepository.findById(lessonId);

        // 2. Lấy thông tin tiến độ học tập từ Database
        Optional<LessonProgress> progressOpt = progressRepository.findByLessonIdAndStudentId(lessonId, studentId);

        if (progressOpt.isPresent()) {
            LessonProgress progress = progressOpt.get();
            response.setWatchedTimeSeconds(progress.getWatchedTimeSeconds());
            response.setLastPosition(progress.getLastPosition());

            // Trạng thái hoàn thành lấy trực tiếp từ DB để bảo toàn thành quả
            boolean isAlreadyCompleted = progress.getIsCompleted();
            response.setIsCompleted(isAlreadyCompleted);

            // 3. TÍNH PHẦN TRĂM HOÀN THÀNH AN TOÀN
            // Nếu đã hoàn thành từ trước, UI luôn mặc định hiển thị 100% (đề phòng video bị lỗi)
            int finalPercentage = isAlreadyCompleted ? 100 : 0;

            // Kiểm tra an toàn Optional: Chỉ tính phần trăm thực tế NẾU hộp có chứa video VÀ thời lượng > 0
            if (videoOpt.isPresent() && videoOpt.get().getDurationSeconds() > 0) {
                int durationVideo = videoOpt.get().getDurationSeconds();

                double rawPercentage = (double) progress.getWatchedTimeSeconds() / durationVideo * 100;
                // Dùng Math.min chặn trần kết quả, đảm bảo không bao giờ vượt quá 100%
                finalPercentage = (int) Math.min(100, Math.round(rawPercentage));
            }

            response.setCompletionPercentage(finalPercentage);

        } else {
            // Trường hợp học viên chưa từng xem bài này
            response.setWatchedTimeSeconds(0);
            response.setLastPosition(0);
            response.setIsCompleted(false);
            response.setCompletionPercentage(0);
        }

        return response;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<CourseMemberProgressResponse> getCourseMembersProgress(String courseId) {
        int totalLessons = lessonRepository.countByCourseId(courseId);

        List<Enrollment> enrollments =
                enrollmentRepository.findByCourseIdAndStatusNot(courseId, EnrollmentStatus.CANCELED);

        List<CourseMemberProgressResponse> result = new ArrayList<>();
        for (Enrollment e : enrollments) {
            User s = e.getStudent();

            int completed = progressRepository
                    .countByStudentIdAndLessonCourseIdAndIsCompletedTrue(s.getId(), courseId);

            int percent = totalLessons == 0 ? 0
                    : (int) Math.round((double) completed / totalLessons * 100);

            result.add(new CourseMemberProgressResponse(
                    s.getId(), s.getFullName(), s.getEmail(), totalLessons, completed, percent));
        }
        return result;
    }
}