package e_learning.assessment_service.feign;


import e_learning.assessment_service.feign.dto.EnrollmentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ENROLLMENT-SERVICE")
public interface EnrollmentServiceFeignClient {
    @GetMapping("/enrollments/student/{studentId}")
    List<EnrollmentDto> getEnrollmentsByStudent(@PathVariable("studentId") Long studentId);

    @GetMapping("/enrollments/{id}")
    EnrollmentDto getEnrollmentById(@PathVariable("id") Long id);
}
