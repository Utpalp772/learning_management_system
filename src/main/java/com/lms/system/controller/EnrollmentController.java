package com.lms.system.controller;

import com.lms.system.dto.EnrollmentRequestDTO;
import com.lms.system.dto.EnrollmentResponseDTO;
import com.lms.system.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // Enroll in a course
    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enroll(
            @Valid @RequestBody EnrollmentRequestDTO requestDTO) {

        EnrollmentResponseDTO response =
                enrollmentService.enroll(requestDTO);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // Get enrollments of the currently logged-in student
    @GetMapping("/my")
    public ResponseEntity<List<EnrollmentResponseDTO>> getMyEnrollments() {

        List<EnrollmentResponseDTO> enrollments =
                enrollmentService.getMyEnrollments();

        return new ResponseEntity<>(
                enrollments,
                HttpStatus.OK
        );
    }

    // Unenroll the currently logged-in student from a course
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> unenroll(
            @PathVariable Long courseId) {

        enrollmentService.unenroll(courseId);

        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT
        );
    }

    // Get all students enrolled in a particular course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsForCourse(
            @PathVariable Long courseId) {

        List<EnrollmentResponseDTO> enrollments =
                enrollmentService.getEnrollmentsForCourse(courseId);

        return new ResponseEntity<>(
                enrollments,
                HttpStatus.OK
        );
    }

    // Remove a particular student from a course
    @DeleteMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<Void> removeStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {

        enrollmentService.removeStudent(courseId, studentId);

        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT
        );
    }
}