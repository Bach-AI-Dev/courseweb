package com.baitapnhom.courseweb.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress" , uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "lesson_id"}) // Tên cột trong Database
})
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private VideoLessons videoLesson;

    @Column(name = "watched_time_seconds")
    private Integer watchedTimeSeconds;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "last_watched_at")
    private LocalDateTime lastWatchedAt;

    @Column(name = "last_position")
    private Integer lastPosition;

    public LessonProgress() {}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public VideoLessons getVideoLesson() {
        return videoLesson;
    }

    public void setVideoLesson(VideoLessons videoLesson) {
        this.videoLesson = videoLesson;
    }

    public Integer getWatchedTimeSeconds() {
        return watchedTimeSeconds;
    }

    public void setWatchedTimeSeconds(Integer watchedTimeSeconds) {
        this.watchedTimeSeconds = watchedTimeSeconds;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public LocalDateTime getLastWatchedAt() {
        return lastWatchedAt;
    }

    public void setLastWatchedAt(LocalDateTime lastWatchedAt) {
        this.lastWatchedAt = lastWatchedAt;
    }

    public Integer getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Integer lastPosition) {
        this.lastPosition = lastPosition;
    }

}
