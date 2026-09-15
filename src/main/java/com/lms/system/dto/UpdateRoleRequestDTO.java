package com.lms.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleRequestDTO {
    @NotBlank(message = "Role is required")
    private String role;
}