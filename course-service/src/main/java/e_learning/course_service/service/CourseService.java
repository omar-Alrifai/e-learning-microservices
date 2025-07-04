package e_learning.course_service.service;
import e_learning.course_service.feign.UserServiceFeignClient;
import e_learning.course_service.feign.dto.UserDto;
import e_learning.course_service.model.Course;
import e_learning.course_service.repository.CourseRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserServiceFeignClient userServiceFeignClient; // حقن Feign Client
    @CircuitBreaker(name = "USER-SERVICE", fallbackMethod = "createCourseFallback")
    public Course createCourse(Course course) {
        //  التحقق من وجود المدرب في User Service
        UserDto instructor = userServiceFeignClient.getUserById(course.getInstructorId());
        if (instructor == null || !"INSTRUCTOR".equals(instructor.getRole())) {
            throw new RuntimeException("Instructor not found or is not an INSTRUCTOR role.");
        }
        course.setStatus("PENDING_APPROVAL"); // تعيين الحالة الأولية عند الإنشاء
        return courseRepository.save(course);
    }

    public Course createCourseFallback(Course course, Throwable t) {
        System.err.println("Fallback executed for createCourse due to: " + t.getMessage());
        course.setStatus("DRAFT_FALLBACK"); // تعيين حالة مختلفة إذا تم اللجوء إلى الفولباك
        return courseRepository.save(course);
    }

    //   للموافقة على الدورة
    public Course approveCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found for approval"));
        course.setStatus("APPROVED");
        return courseRepository.save(course);
    }

    //   لجلب الدورات المعتمدة فقط
    public List<Course> getApprovedCourses() {
        return courseRepository.findByStatus("APPROVED");
    }


    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setInstructorId(courseDetails.getInstructorId());
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}