package e_learning.enrollment_service.feign;

import e_learning.enrollment_service.feign.dto.CourseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "COURSE-SERVICE")
public interface CourseServiceFeignClient {
    @GetMapping("/courses/{id}")
    CourseDto getCourseById(@PathVariable("id") Long id);

    @GetMapping("/courses/approved")
    List<CourseDto> getApprovedCourses();
}
