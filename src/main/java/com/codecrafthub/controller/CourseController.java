package com.codecrafthub.controller;

import com.codecrafthub.model.Course;
import com.codecrafthub.model.CourseStatus;
import com.codecrafthub.service.CourseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseStorageService storage;

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Course created = storage.create(course);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Course> getAll() {
        return storage.findAll();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<Course> courses = storage.findAll();
        Map<String, Integer> coursesByStatus = new LinkedHashMap<>();
        coursesByStatus.put("notStarted", 0);
        coursesByStatus.put("inProgress", 0);
        coursesByStatus.put("completed", 0);

        for (Course course : courses) {
            if (course.getStatus() == CourseStatus.NOT_STARTED) {
                coursesByStatus.compute("notStarted", (key, count) -> count + 1);
            } else if (course.getStatus() == CourseStatus.IN_PROGRESS) {
                coursesByStatus.compute("inProgress", (key, count) -> count + 1);
            } else if (course.getStatus() == CourseStatus.COMPLETED) {
                coursesByStatus.compute("completed", (key, count) -> count + 1);
            }
        }

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCourses", courses.size());
        stats.put("coursesByStatus", coursesByStatus);
        return stats;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getById(@PathVariable String id) {
        Optional<Course> found = storage.findById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> update(@PathVariable String id, @RequestBody Course course) {
        Course updated = storage.update(id, course);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean ok = storage.delete(id);
        if (ok) return ResponseEntity.noContent().build();
        else return ResponseEntity.notFound().build();
    }
}
