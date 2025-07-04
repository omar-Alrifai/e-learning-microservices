package e_learning.course_service.controller;

import e_learning.course_service.model.Course;
import e_learning.course_service.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')") // المدرب أو المسؤول فقط يمكنه إنشاء دورة
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        try {
            Course createdCourse = courseService.createCourse(course);
            return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/admin/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')") // يتطلب دور ADMIN
    public ResponseEntity<Course> approveCourse(@PathVariable Long id) {
        try {
            Course approvedCourse = courseService.approveCourse(id);
            return new ResponseEntity<>(approvedCourse, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT', 'INTERNAL_SERVICE')")// الجميع يمكنهم رؤية دورات فردية
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(course -> new ResponseEntity<>(course, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT', 'INTERNAL_SERVICE')") // الجميع يمكنهم رؤية جميع الدورات
    public ResponseEntity<List<Course>> getAllCourses() {
        return new ResponseEntity<>(courseService.getAllCourses(), HttpStatus.OK);
    }

    @GetMapping("/approved")
    @PreAuthorize("permitAll()") // يمكن للجميع رؤية الدورات المعتمدة فقط
    public ResponseEntity<List<Course>> getApprovedCourses() {
        return new ResponseEntity<>(courseService.getApprovedCourses(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')") // المدرب أو المسؤول فقط يمكنه تحديث دورة
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course courseDetails) {
        Course updatedCourse = courseService.updateCourse(id, courseDetails);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // يتطلب دور ADMIN للحذف
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}