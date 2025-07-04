package e_learning.assessment_service.feign;

import e_learning.assessment_service.feign.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceFeignClient {
    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    @GetMapping("/users/byUsername")
    UserDto getUserByUsername(@RequestParam("username") String username);
}
