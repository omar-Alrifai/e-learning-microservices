package e_learning.assessment_service.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FeignClientInterceptor.class);

    @Value("${internal-service.username}") //  حقن اسم المستخدم الداخلي العام
    private String internalUsername;

    @Value("${internal-service.password}") //  حقن كلمة المرور الداخلية العامة
    private String internalPassword;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String auth = internalUsername + ":" + internalPassword;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        requestTemplate.header("Authorization", authHeader);
        log.debug("Enrollment Feign Client: Adding Authorization header for {}: {}", requestTemplate.feignTarget().name(), authHeader);
        log.debug("Enrollment Feign Client: Request URL: {}", requestTemplate.url());
    }
}