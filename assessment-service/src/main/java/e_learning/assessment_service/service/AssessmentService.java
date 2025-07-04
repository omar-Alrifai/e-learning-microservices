package e_learning.assessment_service.service;
import e_learning.assessment_service.feign.CourseServiceFeignClient;
import e_learning.assessment_service.feign.EnrollmentServiceFeignClient;
import e_learning.assessment_service.feign.UserServiceFeignClient;
import e_learning.assessment_service.feign.dto.CourseDto;
import e_learning.assessment_service.feign.dto.EnrollmentDto;
import e_learning.assessment_service.feign.dto.UserDto;
import e_learning.assessment_service.model.Assessment;
import e_learning.assessment_service.model.Question;
import e_learning.assessment_service.model.StudentAnswer;
import e_learning.assessment_service.model.StudentResult;
import e_learning.assessment_service.repository.AssessmentRepository;
import e_learning.assessment_service.repository.QuestionRepository;
import e_learning.assessment_service.repository.StudentAnswerRepository;
import e_learning.assessment_service.repository.StudentResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private StudentAnswerRepository studentAnswerRepository;
    @Autowired
    private StudentResultRepository studentResultRepository;

    @Autowired
    private CourseServiceFeignClient courseServiceFeignClient;
    @Autowired
    private UserServiceFeignClient userServiceFeignClient;
    @Autowired
    private EnrollmentServiceFeignClient enrollmentServiceFeignClient;

    // المدرب ينشئ اختبار لدورة
    public Assessment createAssessment(Assessment assessment) {
        // التحقق من أن الدورة موجودة ومعتمدة
        CourseDto course = courseServiceFeignClient.getCourseById(assessment.getCourseId());
        if (course == null || !"APPROVED".equals(course.getStatus())) {
            throw new RuntimeException("Course not found or not approved for assessment.");
        }
        //  التحقق من أن المدرب الذي ينشئ الاختبار هو بالفعل مدرب الدورة
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto instructor = userServiceFeignClient.getUserByUsername(currentUsername); // الحصول على المدرب من UserService
        if (instructor == null || !instructor.getId().equals(assessment.getInstructorId()) || !"INSTRUCTOR".equals(instructor.getRole())) {
            throw new RuntimeException("Only the course instructor can create assessments for this course, or instructor not found.");
        }

        return assessmentRepository.save(assessment);
    }

    // المدرب يضيف أسئلة لاختبار
    public Question addQuestionToAssessment(Question question) {
        // التحقق من أن الاختبار موجود
        assessmentRepository.findById(question.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found."));
        return questionRepository.save(question);
    }

    // الطالب يجري الاختبار
    public StudentResult submitAssessment(Long assessmentId, String studentUsername, Map<Long, String> answers) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found."));

        //  الحصول على ID الطالب من UserService بناءً على اسم المستخدم
        UserDto studentUserDto = userServiceFeignClient.getUserByUsername(studentUsername);
        if (studentUserDto == null || !"STUDENT".equals(studentUserDto.getRole())) {
            throw new RuntimeException("Student not found or is not a student.");
        }
        Long studentId = studentUserDto.getId(); //  استخدام ID الطالب الفعلي

        // التحقق من أن الطالب مسجل في الدورة الخاصة بالاختبار
        List<EnrollmentDto> enrollments = enrollmentServiceFeignClient.getEnrollmentsByStudent(studentId);
        boolean isEnrolled = enrollments.stream()
                .anyMatch(enrollment -> enrollment.getCourseId().equals(assessment.getCourseId()) && "ENROLLED".equals(enrollment.getStatus()));
        if (!isEnrolled) {
            throw new RuntimeException("Student is not enrolled in this course or enrollment is not active.");
        }

        // التحقق مما إذا كان الطالب قد أجرى هذا الاختبار من قبل
        Optional<StudentResult> existingResult = studentResultRepository.findByStudentIdAndAssessmentId(studentId, assessmentId);
        if (existingResult.isPresent()) {
            throw new RuntimeException("Student has already submitted this assessment.");
        }

        // ... (بقية منطق تقييم الإجابات وحساب الدرجة وحفظ النتيجة) ...
        List<Question> questions = questionRepository.findByAssessmentId(assessmentId);
        int score = 0;
        for (Question question : questions) {
            String studentChoice = answers.get(question.getId());
            if (studentChoice != null && question.getCorrectAnswer() != null && studentChoice.equalsIgnoreCase(question.getCorrectAnswer())) {
                score++;
            }
            StudentAnswer studentAnswer = new StudentAnswer(null, studentId, assessmentId, question.getId(), studentChoice);
            studentAnswerRepository.save(studentAnswer);
        }

        StudentResult result = new StudentResult();
        result.setStudentId(studentId);
        result.setAssessmentId(assessmentId);
        result.setScore(score);
        result.setPassStatus(score >= assessment.getPassMarks() ? "PASSED" : "FAILED");
        result.setSubmissionDate(LocalDate.now().toString());
        return studentResultRepository.save(result);
    }

    // تتبع حالة المشتركين ونتائج الاختبارات
    public Optional<StudentResult> getStudentResult(Long studentId, Long assessmentId) {
        return studentResultRepository.findByStudentIdAndAssessmentId(studentId, assessmentId);
    }

    public List<StudentResult> getStudentResultsByStudent(Long studentId) {
        return studentResultRepository.findByStudentId(studentId);
    }

    public List<StudentResult> getStudentResultsByAssessment(Long assessmentId) {
        return studentResultRepository.findByAssessmentId(assessmentId);
    }

    // طرق CRUD أساسية
    public Optional<Assessment> getAssessmentById(Long id) {
        return assessmentRepository.findById(id);
    }

    public List<Assessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }

    public void deleteAssessment(Long id) {
        // حذف الأسئلة والنتائج المرتبطة
        questionRepository.deleteByAssessmentId(id);
        studentAnswerRepository.deleteByAssessmentId(id);
        studentResultRepository.deleteByAssessmentId(id);
        assessmentRepository.deleteById(id);
    }


    //  استعراض النتائج بناءً على اسم المستخدم المصادق عليه
    public List<StudentResult> getStudentResultsByStudentUsername(String username) {
        UserDto studentUser = userServiceFeignClient.getUserByUsername(username);
        if (studentUser == null || !"STUDENT".equals(studentUser.getRole())) {
            throw new RuntimeException("Authenticated user is not a valid student.");
        }
        return studentResultRepository.findByStudentId(studentUser.getId());
    }

    //  طريقة مساعدة للمسؤول لرؤية نتائج طالب معين (باستخدام ID)
    public List<StudentResult> getStudentResultsByStudentId(Long studentId) {
        UserDto studentUser = userServiceFeignClient.getUserById(studentId);
        if (studentUser == null || !"STUDENT".equals(studentUser.getRole())) {
            throw new RuntimeException("Student not found or is not a student for this query.");
        }
        return studentResultRepository.findByStudentId(studentId);
    }

    //  استعراض الاختبارات بناءً على اسم المدرب المصادق عليه
    public List<Assessment> getAssessmentsByInstructorUsername(String username) {
        UserDto instructorUser = userServiceFeignClient.getUserByUsername(username);
        if (instructorUser == null || !"INSTRUCTOR".equals(instructorUser.getRole())) {
            throw new RuntimeException("Authenticated user is not a valid instructor.");
        }
        return assessmentRepository.findByInstructorId(instructorUser.getId());
    }

    //  طريقة مساعدة للمسؤول لرؤية اختبارات مدرب معين (باستخدام ID)
    public List<Assessment> getAssessmentsByInstructorId(Long instructorId) {
        UserDto instructorUser = userServiceFeignClient.getUserById(instructorId);
        if (instructorUser == null || !"INSTRUCTOR".equals(instructorUser.getRole())) {
            throw new RuntimeException("Instructor not found or is not an instructor for this query.");
        }
        return assessmentRepository.findByInstructorId(instructorId);
    }


}