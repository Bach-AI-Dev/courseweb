package com.baitapnhom.courseweb.dto.request;

import com.baitapnhom.courseweb.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;

public class EnrollmentRequest {
    @NotNull(message = "STATUS_CANNOT_BE_NULL")
    private EnrollmentStatus status;

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
}