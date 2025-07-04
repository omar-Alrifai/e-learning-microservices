package e_learning.course_service.feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(FeignClientInterceptor.class);
    @Value("${user-service.security.username}")
    private String username;
    @Value("${user-service.security.password}")
    private String password;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        requestTemplate.header("Authorization", authHeader);
        log.debug("Feign Client: Adding Authorization header: {}", authHeader);
        log.debug("Feign Client: Request URL: {}", requestTemplate.url());
    }
}