//package e_learning.enrollment_service;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import java.util.Base64;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import reactor.core.publisher.Mono;
//
//@Component
//public class CommandLineRunnerTest implements CommandLineRunner {
//
//    private static final Logger log = LoggerFactory.getLogger(CommandLineRunnerTest.class);
//
//    @Value("${internal-service.username}")
//    private String internalUsername;
//
//    @Value("${internal-service.password}")
//    private String internalPassword;
//
//    @Override
//    public void run(String... args) throws Exception {
//        log.info("Running CommandLineRunnerTest to directly connect to CourseService...");
//
//        WebClient webClient = WebClient.builder()
//                .baseUrl("http://localhost:8083")
//                .defaultHeaders(headers -> {
//                    String auth = internalUsername + ":" + internalPassword;
//                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
//                    headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
//                    log.debug("CommandLineRunnerTest: Adding Authorization header: Basic {}", encodedAuth);
//                })
//                .build();
//
//        try {
//            String response = webClient.get()
//                    .uri("/courses/1")
//                    .retrieve()
//                    .onStatus(HttpStatus.NOT_FOUND::equals, resp -> {
//                        log.error("CommandLineRunnerTest: Received 404 NOT FOUND from CourseService (direct call). Check if course ID=1 exists.");
//                        return Mono.error(new RuntimeException("Course not found (ID=1)"));
//                    })
//                    .onStatus(HttpStatus.FORBIDDEN::equals, resp -> {
//                        log.error("CommandLineRunnerTest: Received 403 FORBIDDEN from CourseService (direct call). Check @PreAuthorize in CourseService.");
//                        return Mono.error(new RuntimeException("Forbidden from CourseService (direct call)"));
//                    })
//                    .onStatus(HttpStatus.UNAUTHORIZED::equals, resp -> {
//                        log.error("CommandLineRunnerTest: Received 401 UNAUTHORIZED from CourseService (direct call). Check internal_enrollment_service credentials/role in UserService's DataLoader.");
//                        return Mono.error(new RuntimeException("Unauthorized from CourseService (direct call)"));
//                    })
//                    .bodyToMono(String.class)
//                    .block();
//
//            log.info("CommandLineRunnerTest: Successfully received response from CourseService: {}", response);
//        } catch (Exception e) {
//            log.error("CommandLineRunnerTest: Failed to connect to CourseService directly: {}", e.getMessage(), e);
//        }
//        log.info("CommandLineRunnerTest finished.");
//    }
//}