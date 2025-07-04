package e_learning.assessment_service.controller;

import e_learning.assessment_service.model.Assessment;
import e_learning.assessment_service.model.Question;
import e_learning.assessment_service.model.StudentResult;
import e_learning.assessment_service.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    // المدرب ينشئ اختبار
    @PostMapping("/instructor/create")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Assessment> createAssessment(@RequestBody Assessment assessment) {
        //  تم وضع منطق التحقق من المدرب إلى AssessmentService
        try {
            Assessment createdAssessment = assessmentService.createAssessment(assessment);
            return new ResponseEntity<>(createdAssessment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error creating assessment: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // المدرب يضيف أسئلة
    @PostMapping("/instructor/add-question")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Question> addQuestion(@RequestBody Question question) {
        try {
            Question addedQuestion = assessmentService.addQuestionToAssessment(question);
            return new ResponseEntity<>(addedQuestion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error adding question: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // الطالب يجري الاختبار
    @PostMapping("/student/submit/{assessmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentResult> submitAssessment(@PathVariable Long assessmentId, @RequestBody Map<Long, String> answers) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName(); //  الحصول على اسم المستخدم
        try {
            StudentResult result = assessmentService.submitAssessment(assessmentId, currentUsername, answers); //  تمرير اسم المستخدم
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error submitting assessment: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // طرق الاستعراض (للمسؤول، المدرب، الطالب)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT') and (hasRole('ADMIN') or hasRole('INSTRUCTOR') or (hasRole('STUDENT') and #id == authentication.principal.id))")
    public ResponseEntity<Assessment> getAssessmentById(@PathVariable Long id) {
        return assessmentService.getAssessmentById(id)
                .map(assessment -> new ResponseEntity<>(assessment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/student/results/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT') and (hasRole('ADMIN') or (#studentId == authentication.principal.id))")
    public ResponseEntity<List<StudentResult>> getStudentResults(@PathVariable Long studentId) {
        List<StudentResult> results = assessmentService.getStudentResultsByStudent(studentId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }


    @GetMapping("/instructor/my-assessments")
    @PreAuthorize("hasRole('INSTRUCTOR')") //  فقط المدرب المصادق عليه يمكنه رؤية اختباراته
    public ResponseEntity<List<Assessment>> getMyAssessments() {
        // الحصول على اسم المستخدم المصادق عليه من سياق الأمن
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Assessment> assessments = assessmentService.getAssessmentsByInstructorUsername(currentUsername); //  تمرير اسم المستخدم
        return new ResponseEntity<>(assessments, HttpStatus.OK);
    }


    @GetMapping("/admin/instructor-assessments/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Assessment>> getInstructorAssessmentsByAdmin(@PathVariable Long instructorId) {
        List<Assessment> assessments = assessmentService.getAssessmentsByInstructorId(instructorId);
        return new ResponseEntity<>(assessments, HttpStatus.OK);
    }

        @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        return new ResponseEntity<>(assessmentService.getAllAssessments(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/student/my-results")
    @PreAuthorize("hasRole('STUDENT')") //  فقط الطالب المصادق عليه يمكنه رؤية نتائجه
    public ResponseEntity<List<StudentResult>> getMyResults() {
        // الحصول على اسم المستخدم المصادق عليه من سياق الأمن
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<StudentResult> results = assessmentService.getStudentResultsByStudentUsername(currentUsername); //  تمرير اسم المستخدم
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    //  مسار للمسؤول لرؤية نتائج طالب معين
    @GetMapping("/admin/student-results/{studentId}")
    @PreAuthorize("hasRole('ADMIN')") //  فقط المسؤول يمكنه رؤية نتائج أي طالب
    public ResponseEntity<List<StudentResult>> getStudentResultsByAdmin(@PathVariable Long studentId) {
        List<StudentResult> results = assessmentService.getStudentResultsByStudentId(studentId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}