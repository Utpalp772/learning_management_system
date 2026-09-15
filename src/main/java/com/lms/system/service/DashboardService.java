package com.lms.system.service;

import com.lms.system.dto.DashboardResponseDTO;
import com.lms.system.model.Course;
import com.lms.system.model.Role;
import com.lms.system.model.User;
import com.lms.system.repository.CourseRepository;
import com.lms.system.repository.EnrollmentRepository;
import com.lms.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public DashboardResponseDTO getDashboard() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.STUDENT) {
            return buildStudentDashboard(currentUser);
        } else if (currentUser.getRole() == Role.INSTRUCTOR) {
            return buildInstructorDashboard(currentUser);
        } else {
            return buildAdminDashboard(currentUser);
        }
    }

    private DashboardResponseDTO buildStudentDashboard(User student) {
        List<DashboardResponseDTO.CourseSummaryDTO> courses = enrollmentRepository
                .findByStudent(student)
                .stream()
                .map(enrollment -> new DashboardResponseDTO.CourseSummaryDTO(
                        enrollment.getCourse().getId(),
                        enrollment.getCourse().getTitle(),
                        null // enrolledCount not relevant for a student's own view
                ))
                .collect(Collectors.toList());

        return new DashboardResponseDTO(student.getUsername(), student.getRole().name(), courses);
    }

    private DashboardResponseDTO buildInstructorDashboard(User instructor) {
        List<Course> myCourses = courseRepository.findAll()
                .stream()
                .filter(course -> course.getCreatedBy() != null
                        && course.getCreatedBy().getId().equals(instructor.getId()))
                .collect(Collectors.toList());

        List<DashboardResponseDTO.CourseSummaryDTO> courseSummaries = myCourses
                .stream()
                .map(course -> new DashboardResponseDTO.CourseSummaryDTO(
                        course.getId(),
                        course.getTitle(),
                        enrollmentRepository.findByCourse(course).size()
                ))
                .collect(Collectors.toList());

        return new DashboardResponseDTO(instructor.getUsername(), instructor.getRole().name(), courseSummaries);
    }

    private DashboardResponseDTO buildAdminDashboard(User admin) {
        List<DashboardResponseDTO.CourseSummaryDTO> allCourses = courseRepository.findAll()
                .stream()
                .map(course -> new DashboardResponseDTO.CourseSummaryDTO(
                        course.getId(),
                        course.getTitle(),
                        enrollmentRepository.findByCourse(course).size()
                ))
                .collect(Collectors.toList());

        return new DashboardResponseDTO(admin.getUsername(), admin.getRole().name(), allCourses);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}