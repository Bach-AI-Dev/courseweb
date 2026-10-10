package com.baitapnhom.courseweb.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "video_lessons")
public class VideoLessons {
    @Id
    private String id;

    // Liên kết 1-1 với Lesson
    @OneToOne
    @MapsId
    @JoinColumn(name = "lesson_id", columnDefinition = "VARCHAR(36)")
    private Lesson lesson;

    @Column(nullable = false, length = 255) // Bỏ unique = true
    private String url;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}