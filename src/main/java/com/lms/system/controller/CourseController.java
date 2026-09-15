package com.lms.system.controller;

import com.lms.system.dto.CourseRequestDTO;
import com.lms.system.dto.CourseResponseDTO;
import com.lms.system.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // CREATE - POST /api/courses
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO requestDTO) {
        CourseResponseDTO created = courseService.createCourse(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED); // 201
    }

    // READ - GET /api/courses
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK); // 200
    }

    // READ - GET /api/courses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        CourseResponseDTO course = courseService.getCourseById(id);
        return new ResponseEntity<>(course, HttpStatus.OK); // 200
    }

    // UPDATE - PUT /api/courses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO requestDTO) {
        CourseResponseDTO updated = courseService.updateCourse(id, requestDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK); // 200
    }

    // DELETE - DELETE /api/courses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
    }
}