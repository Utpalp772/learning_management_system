package com.lms.system.repository;

import com.lms.system.model.Course;
import com.lms.system.model.Enrollment;
import com.lms.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    boolean existsByStudentAndCourse(User student, Course course);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    Optional<Enrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);
}