package com.lms.system.service;

import com.lms.system.dto.CourseRequestDTO;
import com.lms.system.dto.CourseResponseDTO;
import com.lms.system.model.Course;
import com.lms.system.model.Enrollment;
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
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {
        Course course = new Course();
        course.setTitle(requestDTO.getTitle());
        course.setDescription(requestDTO.getDescription());
        course.setInstructor(requestDTO.getInstructor());
        course.setDuration(requestDTO.getDuration());
        course.setCreatedBy(getCurrentUser());

        Course saved = courseRepository.save(course);
        return mapToResponseDTO(saved);
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return mapToResponseDTO(course);
    }

    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO requestDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        checkOwnershipOrAdmin(course);

        course.setTitle(requestDTO.getTitle());
        course.setDescription(requestDTO.getDescription());
        course.setInstructor(requestDTO.getInstructor());
        course.setDuration(requestDTO.getDuration());

        Course updated = courseRepository.save(course);
        return mapToResponseDTO(updated);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        checkOwnershipOrAdmin(course);

        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);
        enrollmentRepository.deleteAll(enrollments);

        courseRepository.deleteById(id);
    }

    private void checkOwnershipOrAdmin(Course course) {
        User currentUser = getCurrentUser();
        boolean isOwner = course.getCreatedBy() != null
                && course.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You can only manage courses you created");
        }
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CourseResponseDTO mapToResponseDTO(Course course) {
        return new CourseResponseDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getInstructor(),
                course.getDuration(),
                course.getCreatedAt()
        );
    }
}