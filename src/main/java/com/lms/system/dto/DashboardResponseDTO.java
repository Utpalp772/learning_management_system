package com.lms.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {

    private String username;
    private String role;
    private List<CourseSummaryDTO> courses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseSummaryDTO {
        private Long courseId;
        private String title;
        private Integer enrolledCount; // only meaningful for instructor view; null for student view
    }
}