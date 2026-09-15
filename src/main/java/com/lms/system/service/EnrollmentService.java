package com.lms.system.service;

import com.lms.system.dto.EnrollmentRequestDTO;
import com.lms.system.dto.EnrollmentResponseDTO;
import com.lms.system.model.Course;
import com.lms.system.model.Enrollment;
import com.lms.system.model.Role;
import com.lms.system.model.User;
import com.lms.system.repository.CourseRepository;
import com.lms.system.repository.EnrollmentRepository;
import com.lms.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    // Enroll current student in a course
    public EnrollmentResponseDTO enroll(EnrollmentRequestDTO requestDTO) {

        User currentStudent = getCurrentUser();

        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (enrollmentRepository.existsByStudentAndCourse(currentStudent, course)) {
            throw new RuntimeException("Already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(currentStudent);
        enrollment.setCourse(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return mapToResponseDTO(savedEnrollment);
    }

    // Get all courses in which current student is enrolled
    public List<EnrollmentResponseDTO> getMyEnrollments() {

        User currentStudent = getCurrentUser();

        return enrollmentRepository.findByStudent(currentStudent)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Unenroll current student from a course
    public void unenroll(Long courseId) {

        User currentStudent = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment =
                enrollmentRepository.findByStudentAndCourse(
                        currentStudent,
                        course
                ).orElseThrow(
                        () -> new RuntimeException(
                                "You are not enrolled in this course"
                        )
                );

        enrollmentRepository.delete(enrollment);
    }

    // Get all students enrolled in a course
    // Only course owner or ADMIN can access this
    public List<EnrollmentResponseDTO> getEnrollmentsForCourse(Long courseId) {

        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean isOwner =
                course.getCreatedBy() != null
                        && course.getCreatedBy().getId().equals(currentUser.getId());

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException(
                    "You can only view enrollments for your own courses"
            );
        }

        return enrollmentRepository.findByCourse(course)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Remove a student from a course
    // Only course owner or ADMIN can perform this
    public void removeStudent(Long courseId, Long studentId) {

        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean isOwner =
                course.getCreatedBy() != null
                        && course.getCreatedBy().getId().equals(currentUser.getId());

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException(
                    "You can only manage enrollments for your own courses"
            );
        }

        Enrollment enrollment =
                enrollmentRepository.findByCourseIdAndStudentId(
                        courseId,
                        studentId
                ).orElseThrow(
                        () -> new RuntimeException("Enrollment not found")
                );

        enrollmentRepository.delete(enrollment);
    }

    // Get currently logged-in user
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {

            throw new RuntimeException("User is not authenticated");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );
    }

    // Convert Enrollment entity to EnrollmentResponseDTO
    private EnrollmentResponseDTO mapToResponseDTO(
            Enrollment enrollment) {

        return new EnrollmentResponseDTO(
                enrollment.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getUsername(),
                enrollment.getEnrolledAt()
        );
    }
}