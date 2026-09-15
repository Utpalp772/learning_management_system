package com.lms.system.repository;

import com.lms.system.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // No implementation needed — Spring Data JPA generates it automatically.
    // We get save(), findAll(), findById(), deleteById(), etc. for free.
}