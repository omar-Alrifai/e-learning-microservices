package e_learning.enrollment_service.service;
import e_learning.enrollment_service.feign.CourseServiceFeignClient;
import e_learning.enrollment_service.feign.UserServiceFeignClient;
import e_learning.enrollment_service.feign.dto.CourseDto;
import e_learning.enrollment_service.feign.dto.UserDto;
import e_learning.enrollment_service.model.Enrollment;
import e_learning.enrollment_service.repository.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserServiceFeignClient userServiceFeignClient;

    @Autowired
    private CourseServiceFeignClient courseServiceFeignClient;

    public Enrollment createEnrollment(Long courseId) {
        // التحقق من الطالب الحالي (يأتي من JWT)
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto student = userServiceFeignClient.getUserByUsername(currentUsername);

        if (student == null || !"STUDENT".equals(student.getRole())) {
            throw new RuntimeException("Only students can enroll in courses, or student user not found.");
        }

        //  التحقق من الاشتراك المزدوج هنا
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
        if (existingEnrollment.isPresent()) {
            throw new RuntimeException("Student is already enrolled in this course.");
        }

        // التحقق من وجود الدورة وأنها معتمدة
        CourseDto course = courseServiceFeignClient.getCourseById(courseId);
        System.out.println("Course object from Feign (EnrollmentService): " + course);

        if (course == null || !"APPROVED".equals(course.getStatus())) {
            throw new RuntimeException("Course not found or not approved for enrollment.");
        }

        // محاكاة عملية الدفع
        double coursePrice = 100.00; // سعر وهمي
        String paymentStatus = "PAID"; // افتراض الدفع ناجح

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.getId());
        enrollment.setCourseId(courseId);
        enrollment.setEnrollmentDate(LocalDate.now().toString());
        enrollment.setStatus("ENROLLED");
        enrollment.setPaymentAmount(coursePrice);
        enrollment.setPaymentStatus(paymentStatus);

        return enrollmentRepository.save(enrollment);
    }

    public Optional<Enrollment> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public Enrollment updateEnrollmentStatus(Long id, String newStatus) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus(newStatus);
        return enrollmentRepository.save(enrollment);
    }

    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }


    //  استعراض تسجيلات الطالب بناءً على اسم المستخدم المصادق عليه
    public List<Enrollment> getEnrollmentsByStudentUsername(String username) {
        UserDto studentUser = userServiceFeignClient.getUserByUsername(username);
        if (studentUser == null || !"STUDENT".equals(studentUser.getRole())) {
            throw new RuntimeException("Authenticated user is not a valid student.");
        }
        return enrollmentRepository.findByStudentId(studentUser.getId());
    }

    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        //  إضافة تحقق هنا للتأكد من وجود الطالب ودوره
        UserDto student = userServiceFeignClient.getUserById(studentId);
        if (student == null || !"STUDENT".equals(student.getRole())) {
            throw new RuntimeException("Student not found or is not a student for this enrollment query.");
        }
        return enrollmentRepository.findByStudentId(studentId);
    }

}
