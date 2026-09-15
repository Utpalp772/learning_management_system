package com.lms.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CourseRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Instructor name is required")
    private String instructor;

    @Positive(message = "Duration must be a positive number")
    private Integer duration;
}