package com.baitapnhom.courseweb.service;

import com.baitapnhom.courseweb.dto.request.LessonRequest;
import com.baitapnhom.courseweb.dto.response.LessonResponse;
import com.baitapnhom.courseweb.entity.Course;
import com.baitapnhom.courseweb.entity.Lesson;
import com.baitapnhom.courseweb.entity.VideoLessons;
import com.baitapnhom.courseweb.repository.CourseRepository;
import com.baitapnhom.courseweb.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseRepository courseRepository;

    // GET: Lấy danh sách bài học theo khóa
    public List<LessonResponse> getLessonsByCourseId(String courseId) {
        List<Lesson> lessonEntities = lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
        List<LessonResponse> lessonList = new ArrayList<>();

        for(Lesson entity: lessonEntities){
            LessonResponse response = new LessonResponse();
            response.setId(entity.getId());

            if (entity.getCourse() != null){
                response.setCourseId(entity.getCourse().getId());
            }

            response.setName(entity.getName());
            response.setType(entity.getType());
            response.setLessonOrder(entity.getLessonOrder());
            if ("VIDEO".equalsIgnoreCase(entity.getType()) && entity.getVideoLesson() != null) {
                response.setVideoUrl(entity.getVideoLesson().getUrl());
                response.setDuration(entity.getVideoLesson().getDurationSeconds());
            }

            lessonList.add(response);
        }
        return lessonList;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    // POST: Tạo lesson mới
    public LessonResponse createLesson(String courseId, LessonRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        Lesson lessonEntity = new Lesson();
        lessonEntity.setCourse(course);
        lessonEntity.setName(request.getName());
        lessonEntity.setType(request.getType()); // "VIDEO" hoặc "ASSIGNMENT"
        lessonEntity.setLessonOrder(request.getLessonOrder());
        if ("VIDEO".equalsIgnoreCase(request.getType())) {
            VideoLessons video = new VideoLessons(); // Tạo 1 video mới
            video.setUrl(request.getVideoUrl());   // Lấy link video khách gửi
            video.setDurationSeconds(request.getDuration());


            lessonEntity.setVideoLesson(video);
        }

        Lesson savedLesson = lessonRepository.save(lessonEntity);

        LessonResponse response = new LessonResponse();
        response.setId(savedLesson.getId());
        response.setCourseId(course.getId());
        response.setName(savedLesson.getName());
        response.setType(savedLesson.getType());
        response.setLessonOrder(savedLesson.getLessonOrder());

        return response;
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    // PUT: Cập nhật bài học
    public LessonResponse updateLesson(String id, LessonRequest request) {
        // 1. Lấy bài học cũ từ tủ lạnh (Database)
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học có ID: " + id));

        // 2. Cập nhật các thông tin cơ bản
        existingLesson.setName(request.getName());
        existingLesson.setType(request.getType());
        existingLesson.setLessonOrder(request.getLessonOrder());

        // 3. XỬ LÝ RIÊNG CHO VIDEO
        if ("VIDEO".equalsIgnoreCase(request.getType())) {
            // Nếu bài học đã có video từ trước -> Lấy ra dùng tiếp, nếu chưa có -> Tạo mới
            VideoLessons video = existingLesson.getVideoLesson();
            if (video == null) {
                video = new VideoLessons();
            }

            // Cập nhật link và thời lượng mới
            video.setUrl(request.getVideoUrl());
            video.setDurationSeconds(request.getDuration());

            // Dán lại vào Bài học
            existingLesson.setVideoLesson(video);
        } else {
            // Trường hợp người dùng đổi từ VIDEO sang ASSIGNMENT -> Gỡ bỏ video
            existingLesson.setVideoLesson(null);
        }

        // 4. Lưu lại vào Database
        Lesson updatedLesson = lessonRepository.save(existingLesson);

        // 5. Đóng gói trả về cho khách (Response)
        LessonResponse response = new LessonResponse();
        response.setId(updatedLesson.getId());

        if (updatedLesson.getCourse() != null) {
            response.setCourseId(updatedLesson.getCourse().getId());
        }

        response.setName(updatedLesson.getName());
        response.setType(updatedLesson.getType());
        response.setLessonOrder(updatedLesson.getLessonOrder());

        // Lấy thông tin video nhét vào hộp nếu nó là bài giảng Video
        if ("VIDEO".equalsIgnoreCase(updatedLesson.getType()) && updatedLesson.getVideoLesson() != null) {
            response.setVideoUrl(updatedLesson.getVideoLesson().getUrl());
            response.setDuration(updatedLesson.getVideoLesson().getDurationSeconds());
        }

        return response;
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    // DELETE: Xóa bài học
    public void deleteLesson(String id) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không thể xóa. Không tìm thấy bài học có ID: " + id));
        lessonRepository.delete(existingLesson);
    }

    // GET: 1 bài
    public LessonResponse getLessonById(String id){
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học có ID: " + id));
        LessonResponse response = new LessonResponse();
        response.setId(existingLesson.getId());

        if (existingLesson.getCourse() != null){
            response.setCourseId(existingLesson.getCourse().getId());
        }

        response.setName(existingLesson.getName());
        response.setType(existingLesson.getType());
        response.setLessonOrder(existingLesson.getLessonOrder());

        if ("VIDEO".equalsIgnoreCase(existingLesson.getType()) && existingLesson.getVideoLesson() != null) {
            response.setVideoUrl(existingLesson.getVideoLesson().getUrl());
            response.setDuration(existingLesson.getVideoLesson().getDurationSeconds());
        }

        return response;
    }
}
