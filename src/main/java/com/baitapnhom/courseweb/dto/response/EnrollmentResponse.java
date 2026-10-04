package com.baitapnhom.courseweb.dto.response;

import java.time.LocalDateTime;

public class EnrollmentResponse {

    private String id;
    private String courseTitle;
    private String studentName;
    private String studentEmail;
    private String status;
    private LocalDateTime enrollDate;

    public EnrollmentResponse() {
    }

    public EnrollmentResponse(String id, String courseTitle, String studentName,
                             String studentEmail, String status, LocalDateTime enrollDate) {
        this.id = id;
        this.courseTitle = courseTitle;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.status = status;
        this.enrollDate = enrollDate;
    }
    
    public EnrollmentResponse(String id, String courseTitle, String status, LocalDateTime enrollDate) {
        this.id = id;
        this.courseTitle = courseTitle;
        this.status = status;
        this.enrollDate = enrollDate;
    }

    public String getId() {
        return id;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getEnrollDate() {
        return enrollDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEnrollDate(LocalDateTime enrollDate) {
        this.enrollDate = enrollDate;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }
}
