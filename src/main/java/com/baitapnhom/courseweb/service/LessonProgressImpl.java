package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.LessonProgressRequest;
import com.baitapnhom.courseweb.dto.response.CourseProgressResponse;
import com.baitapnhom.courseweb.dto.response.LessonProgressResponse;
import com.baitapnhom.courseweb.entity.LessonProgress;
import com.baitapnhom.courseweb.entity.VideoLessons;
import com.baitapnhom.courseweb.exception.AppException;
import com.baitapnhom.courseweb.exception.ErrorCode;
import com.baitapnhom.courseweb.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // THÊM DÒNG NÀY ĐỂ IMPORT THỜI GIAN
import java.util.Optional;

@Service
@Transactional
public class LessonProgressImpl implements ILessonProgressService {

    private final LessonProgressRepository progressRepository;
    private final VideoLessonsRepository videoRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;

    public LessonProgressImpl(LessonProgressRepository progressRepository,
                              VideoLessonsRepository videoRepository,
                              StudentRepository studentRepository,
                              EnrollmentRepository enrollmentRepository,LessonRepository lessonRepository) {
        this.progressRepository = progressRepository;
        this.videoRepository = videoRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public LessonProgressResponse updateProgress(LessonProgressRequest request){

        Optional<VideoLessons> videoRequest = videoRepository.findById(request.getLessonId());

        if(videoRequest.isEmpty()) {
            throw new AppException(ErrorCode.VIDEO_NOT_FOUND);
        }

        VideoLessons video = videoRequest.get();

        String courseId = video.getLesson().getCourse().getId();

        boolean isEnrolled = enrollmentRepository.existsByStudentStudentIdAndCourseId(request.getStudentId(), courseId);
        if(!isEnrolled) {
            throw new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        int durationVideo = video.getDurationSeconds();
        if (durationVideo <= 0) {
            durationVideo = 1;
        }

        Optional<LessonProgress> progressRequest = progressRepository.findByVideoLesson_LessonIdAndStudent_StudentId(request.getLessonId(), request.getStudentId());

        int tolerance = 2;
        LessonProgress savedProgress;

        int validTimeDelta = Math.max(request.getTimeDelta(), 0);

        if(progressRequest.isPresent()){
            LessonProgress progress = progressRequest.get();

            int newWatchedTimeSeconds = progress.getWatchedTimeSeconds() + validTimeDelta;
            newWatchedTimeSeconds = Math.min(newWatchedTimeSeconds, durationVideo);

            int newLastPosition = Math.min(request.getLastPosition(), durationVideo);

            if(newWatchedTimeSeconds >= (durationVideo - tolerance) && !progress.getIsCompleted() && newLastPosition >= (durationVideo-tolerance)){
                progress.setIsCompleted(true);
            }

            progress.setWatchedTimeSeconds(newWatchedTimeSeconds);
            progress.setLastPosition(newLastPosition);

            // THÊM DÒNG NÀY: Cập nhật thời gian xem lần cuối (cho tiến độ cũ)
            progress.setLastWatchedAt(LocalDateTime.now());

            savedProgress = progressRepository.save(progress);
        }
        else{
            LessonProgress newProgress = new LessonProgress();

            newProgress.setVideoLesson(videoRepository.getReferenceById(request.getLessonId()));
            newProgress.setStudent(studentRepository.getReferenceById(request.getStudentId()));

            int initalTime = Math.min(validTimeDelta, durationVideo);
            newProgress.setWatchedTimeSeconds(initalTime);

            int newLastPosition = Math.min(request.getLastPosition(), durationVideo);
            newProgress.setLastPosition(newLastPosition);

            if(initalTime >= (durationVideo - tolerance) && newLastPosition >= (durationVideo - tolerance)){
                newProgress.setIsCompleted(true);
            }
            else{
                newProgress.setIsCompleted(false);
            }

            // THÊM DÒNG NÀY: Cập nhật thời gian xem lần cuối (cho tiến độ mới tạo)
            newProgress.setLastWatchedAt(LocalDateTime.now());

            savedProgress = progressRepository.save(newProgress);
        }

        double percentage = (double) savedProgress.getWatchedTimeSeconds() / durationVideo * 100;
        int percentCompleted = (int) Math.round(percentage);

        LessonProgressResponse responseDto = new LessonProgressResponse();
        responseDto.setStudentId(request.getStudentId());
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

        int completedLessons = progressRepository.countByStudentStudentIdAndVideoLessonLessonCourseIdAndIsCompletedTrue(studentId, courseId);

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

        Optional<LessonProgress> progressOpt = progressRepository.findByVideoLesson_LessonIdAndStudent_StudentId(lessonId, studentId);

        if (progressOpt.isPresent()) {
            LessonProgress progress = progressOpt.get();
            response.setWatchedTimeSeconds(progress.getWatchedTimeSeconds());
            response.setLastPosition(progress.getLastPosition());
            response.setIsCompleted(progress.getIsCompleted());

        } else {
            response.setWatchedTimeSeconds(0);
            response.setLastPosition(0);
            response.setIsCompleted(false);
            response.setCompletionPercentage(0);
        }

        return response;
    }
}