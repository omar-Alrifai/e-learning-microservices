package e_learning.user_service.service;
import e_learning.user_service.model.User;
import e_learning.user_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // إنشاء المستخدمين إذا لم يكونوا موجودين
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setRole("ADMIN");
            admin.setEmail("admin@e_learning.com");
            userRepository.save(admin);
            System.out.println("Admin user created.");
        }

        if (userRepository.findByUsername("instructor") == null) {
            User instructor = new User();
            instructor.setUsername("instructor");
            instructor.setPassword(passwordEncoder.encode("instructorpass"));
            instructor.setRole("INSTRUCTOR");
            instructor.setEmail("instructor@e_learning.com");
            userRepository.save(instructor);
            System.out.println("Instructor user created.");
        }

        if (userRepository.findByUsername("student") == null) {
            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("studentpass"));
            student.setRole("STUDENT");
            student.setEmail("student@e_learning.com");
            userRepository.save(student);
            System.out.println("Default Student user created.");
        }
        if (userRepository.findByUsername("internal_course_service") == null) {
            User internalServiceUser = new User();
            internalServiceUser.setUsername("internal_course_service");
            internalServiceUser.setPassword(passwordEncoder.encode("secure_internal_pass"));
            internalServiceUser.setRole("INTERNAL_SERVICE");
            internalServiceUser.setEmail("internal.service@e_learning.com");
            userRepository.save(internalServiceUser);
            System.out.println("Internal Course Service user created.");
        }

        //  هذا المستخدم لخدمة الاشتراك والدفع
        if (userRepository.findByUsername("internal_enrollment_service") == null) {
            User internalServiceEnrollmentUser = new User();
            internalServiceEnrollmentUser.setUsername("internal_enrollment_service");
            internalServiceEnrollmentUser.setPassword(passwordEncoder.encode("enrollment_pass"));
            internalServiceEnrollmentUser.setRole("INTERNAL_SERVICE");
            internalServiceEnrollmentUser.setEmail("internal.enrollment.service@e_learning.com");
            userRepository.save(internalServiceEnrollmentUser);
            System.out.println("Internal Enrollment Service user created.");
        }
        if (userRepository.findByUsername("internal_assessment_service") == null) {
            User internalAssessmentServiceUser = new User();
            internalAssessmentServiceUser.setUsername("internal_assessment_service");
            internalAssessmentServiceUser.setPassword(passwordEncoder.encode("assessment_pass"));
            internalAssessmentServiceUser.setRole("INTERNAL_SERVICE");
            internalAssessmentServiceUser.setEmail("internal.assessment.service@e_learning.com");
            userRepository.save(internalAssessmentServiceUser);
            System.out.println("Internal Assessment Service user created.");
        }
    }
}
