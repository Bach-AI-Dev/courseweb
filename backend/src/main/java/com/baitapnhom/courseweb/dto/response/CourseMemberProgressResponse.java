package com.baitapnhom.courseweb.dto.response;

public record CourseMemberProgressResponse(
        String studentId,
        String fullName,
        String email,
        int totalLessons,
        int completedLessons,
        int progressPercentage) {
}