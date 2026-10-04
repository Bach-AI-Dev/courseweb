package com.baitapnhom.courseweb.dto.response;


public class LessonProgressResponse {
    private String studentId;
    private int lastPosition;
    private String lessonId;
    private Integer watchedTimeSeconds;
    private Boolean isCompleted;
    private Integer completionPercentage;
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
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

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }
}