package e_learning.course_service.feign;

import e_learning.course_service.feign.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "USER-SERVICE")//"name" يُفعل Load Balancing مع Eureka
public interface UserServiceFeignClient {
    @GetMapping("/users/{id}") // تحديد نقطة النهاية في User Service
    UserDto getUserById(@PathVariable("id") Long id);
}
