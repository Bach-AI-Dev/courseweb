package com.baitapnhom.courseweb.dto.response;

public class CourseEnrollmentSummaryResponse {
    private String courseId;
    private long activeCount;
    private long completedCount;
    private long canceledCount;
    private double completionRate; // % = COMPLETED / (ACTIVE + COMPLETED) * 100

    public CourseEnrollmentSummaryResponse() {}

    public CourseEnrollmentSummaryResponse(String courseId, long activeCount, long completedCount, long canceledCount, double completionRate) {
        this.courseId = courseId;
        this.activeCount = activeCount;
        this.completedCount = completedCount;
        this.canceledCount = canceledCount;
        this.completionRate = completionRate;
    }

    // Getters & Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public long getActiveCount() { return activeCount; }
    public void setActiveCount(long activeCount) { this.activeCount = activeCount; }
    public long getCompletedCount() { return completedCount; }
    public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }
    public long getCanceledCount() { return canceledCount; }
    public void setCanceledCount(long canceledCount) { this.canceledCount = canceledCount; }
    public double getCompletionRate() { return completionRate; }
    public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }
}