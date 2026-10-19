package com.baitapnhom.courseweb.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LessonProgressRequest {

    private String studentId;

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

    public Integer getTimeDelta() {
        return timeDelta;
    }

    public void setTimeDelta(Integer timeDelta) {
        this.timeDelta = timeDelta;
    }

    public Integer getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Integer lastPosition) {
        this.lastPosition = lastPosition;
    }

    @NotNull(message = "lessonId không được để trống")
    private String lessonId;

    @NotNull(message = "Thời gian không được để trống")
    private Integer timeDelta;

    @NotNull(message = "Vi tri khong duoc de trong")
    @Min(value = 0,message = "Vi tri khong duoc am")
    private  Integer lastPosition;
}
