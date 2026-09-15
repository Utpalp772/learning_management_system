package com.lms.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequestDTO {

    @NotNull(message = "Course ID is required")
    private Long courseId;
}