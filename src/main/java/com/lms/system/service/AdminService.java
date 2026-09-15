package com.lms.system.service;

import com.lms.system.dto.UpdateRoleRequestDTO;
import com.lms.system.dto.UserSummaryDTO;
import com.lms.system.model.Course;
import com.lms.system.model.Role;
import com.lms.system.model.User;
import com.lms.system.repository.CourseRepository;
import com.lms.system.repository.EnrollmentRepository;
import com.lms.system.repository.OtpTokenRepository;
import com.lms.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    public UserSummaryDTO updateUserRole(Long userId, UpdateRoleRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole;
        try {
            newRole = Role.valueOf(requestDTO.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Must be STUDENT, INSTRUCTOR, or ADMIN");
        }

        user.setRole(newRole);
        User updated = userRepository.save(user);
        return mapToSummaryDTO(updated);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove any enrollments this user has as a student
        enrollmentRepository.deleteAll(
                enrollmentRepository.findByStudent(user)
        );

        // Remove any courses this user created, along with each course's enrollments
        List<Course> ownedCourses = courseRepository.findAll()
                .stream()
                .filter(c -> c.getCreatedBy() != null && c.getCreatedBy().getId().equals(user.getId()))
                .collect(Collectors.toList());

        for (Course course : ownedCourses) {
            enrollmentRepository.deleteAll(enrollmentRepository.findByCourse(course));
            courseRepository.delete(course);
        }

        // Remove any OTP tokens tied to this user
        otpTokenRepository.deleteAll(otpTokenRepository.findByUser(user));

        userRepository.delete(user);
    }

    private UserSummaryDTO mapToSummaryDTO(User user) {
        return new UserSummaryDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.isEmailVerified()
        );
    }
}