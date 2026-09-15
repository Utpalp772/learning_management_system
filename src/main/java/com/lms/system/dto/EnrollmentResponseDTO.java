package com.lms.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDTO {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private Long studentId;
    private String studentUsername;
    private LocalDateTime enrolledAt;
}