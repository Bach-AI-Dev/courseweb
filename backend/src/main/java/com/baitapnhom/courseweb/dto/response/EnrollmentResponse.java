package com.baitapnhom.courseweb.dto.response;

import java.time.LocalDateTime;

public class EnrollmentResponse {

    private String id;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String courseId;
    private String courseTitle;
    private String status;
    private LocalDateTime enrollDate;

    public EnrollmentResponse() {
    }

    public EnrollmentResponse(String id, String studentId, String studentName, String studentEmail,
                              String courseId, String courseTitle, String status, LocalDateTime enrollDate) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.status = status;
        this.enrollDate = enrollDate;
    }

    public EnrollmentResponse(String id, String courseId, String courseTitle, String status, LocalDateTime enrollDate) {
        this.id = id;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.status = status;
        this.enrollDate = enrollDate;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(LocalDateTime enrollDate) {
        this.enrollDate = enrollDate;
    }
}