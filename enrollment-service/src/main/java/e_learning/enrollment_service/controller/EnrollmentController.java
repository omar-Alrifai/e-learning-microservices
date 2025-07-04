package e_learning.enrollment_service.controller;
import e_learning.enrollment_service.model.Enrollment;
import e_learning.enrollment_service.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/student/enroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')") // فقط الطلاب يمكنهم التسجيل
    public ResponseEntity<Enrollment> enrollInCourse(@PathVariable Long courseId) {
        try {
            Enrollment enrollment = enrollmentService.createEnrollment(courseId);
            return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error during enrollment: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT') and (#id == authentication.principal.id or hasRole('ADMIN'))")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Long id) {
        return enrollmentService.getEnrollmentById(id)
                .map(enrollment -> new ResponseEntity<>(enrollment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'INTERNAL_SERVICE') and (hasRole('ADMIN') or hasRole('INTERNAL_SERVICE') or (#studentId == authentication.principal.id))")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @GetMapping("/student/my-enrollments")
    @PreAuthorize("hasRole('STUDENT')") //  فقط الطالب المصادق عليه يمكنه رؤية تسجيلاته
    public ResponseEntity<List<Enrollment>> getMyEnrollments() {
        // الحصول على اسم المستخدم المصادق عليه من سياق الأمن
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudentUsername(currentUsername); //  تمرير اسم المستخدم
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @GetMapping("/admin/student-enrollments/{studentId}") //  مسار للمسؤول فقط
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Enrollment>> getStudentEnrollmentsByAdmin(@PathVariable Long studentId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }



    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }

    @PutMapping("/{id}/status/{newStatus}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Enrollment> updateEnrollmentStatus(@PathVariable Long id, @PathVariable String newStatus) {
        Enrollment updatedEnrollment = enrollmentService.updateEnrollmentStatus(id, newStatus);
        return new ResponseEntity<>(updatedEnrollment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
