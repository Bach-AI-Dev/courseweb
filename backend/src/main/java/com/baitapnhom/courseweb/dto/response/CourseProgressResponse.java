package com.baitapnhom.courseweb.dto.response;

public class CourseProgressResponse {
    private String courseId;
    private String studentId;
    private int totalLessons;
    private int completedLessons;
    private int progressPercentage;

    // 1. Constructor rỗng (Bắt buộc phải có để Spring Boot phân tích dữ liệu)
    public CourseProgressResponse() {
    }

    // 2. Các hàm Getter / Setter để lấy và gán giá trị
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public int getCompletedLessons() {
        return completedLessons;
    }

    public void setCompletedLessons(int completedLessons) {
        this.completedLessons = completedLessons;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}