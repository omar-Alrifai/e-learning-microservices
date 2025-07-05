# E-Learning Microservices System

## 🚀 Project Overview

This project implements a distributed E-Learning platform using **Spring Boot** and **Spring Cloud** microservices architecture. The system is designed to be scalable, resilient, and manageable, covering core functionalities for an online education environment. It streamlines educational processes by managing various user roles, course publishing, student enrollments with simulated payments, and comprehensive assessment management.

## ✨ Key Features & Achieved Requirements

This system fulfills the following core requirements of the task:

1.  **Microservices Architecture Design:**
    * **Description:** The application is broken down into independent, loosely coupled microservices, each focusing on a specific business capability (e.g., User, Course, Enrollment, Assessment).
    * **Components:** Eureka Server, API Gateway, User Service, Course Service, Enrollment Service, Assessment Service.

2.  **RESTful Web Services Implementation:**
    * **Description:** All microservices expose RESTful APIs, enabling communication via standard HTTP methods (GET, POST, PUT, DELETE) and JSON data exchange.
    * **Example:** User management endpoints in `User Service`.
    * **Key Code:** `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping` in Controller classes.

3.  **Centralized API Gateway:**
    * **Description:** A single entry point (`API Gateway`) handles all external client requests, routing them dynamically to the appropriate backend microservice. This simplifies client interactions and provides centralized control.
    * **Key Code:** Route configurations in `api-gateway/src/main/resources/application.properties`.
    * **Example:** `spring.cloud.gateway.routes[0].uri=lb://USER-SERVICE`

4.  **Dynamic Service Discovery:**
    * **Description:** Services dynamically register themselves and discover other services using `Eureka Server`. This removes the need for hardcoding service locations, making the system highly flexible and resilient to changes in deployment.
    * **Components:** `Eureka Server` (the registry) and `Eureka Clients` (all other microservices).
    * **Key Code:** `@EnableEurekaServer` (for server), `@EnableDiscoveryClient` (for clients), `eureka.client.service-url`.

5.  **Secure Inter-Service Communication:**
    * **Description:** Microservices communicate securely with each other using dynamic addresses and authenticated calls. `Feign Clients` simplify HTTP calls, and internal `Basic Authentication` ensures that only authorized services can interact.
    * **Mechanism:** `FeignClientInterceptor` automatically adds `Basic Auth` headers (e.g., `internal_service:password`) to outgoing requests.
    * **Key Code:** `@FeignClient`, `FeignClientInterceptor.java`, `internal-service.username/password` in `application.properties` (for credentials), `@PreAuthorize("... 'INTERNAL_SERVICE'")` for authorization.

6.  **Fault Tolerance with Circuit Breaker:**
    * **Description:** The system implements the Circuit Breaker pattern using `Resilience4j` to prevent cascading failures. If a downstream service (e.g., User Service) becomes unresponsive, the Circuit Breaker "opens" to stop sending requests, allowing the failing service to recover and protecting the calling service from resource exhaustion.
    * **Implementation:** Applied in `Course Service` when calling `User Service`.
    * **Key Code:** `@CircuitBreaker`, `fallbackMethod` in `CourseService.java`, `resilience4j.circuitbreaker.instances` properties.

7.  **Client-Side Load Balancing:**
    * **Description:** Requests are automatically distributed among multiple running instances of a service, enhancing system scalability and availability.
    * **Mechanism:** `Spring Cloud LoadBalancer` selects a healthy service instance from those registered with Eureka. This is used by `API Gateway` for external calls and by `Feign Clients` for internal calls.
    * **Key Code:** `lb://SERVICE_NAME` in `API Gateway` routes, `@FeignClient(name="SERVICE_NAME")` in Feign interfaces.

---

## 🛠️ Technologies Used

* **Backend Framework:** Java 21, Spring Boot (v3.3.1)
* **Spring Cloud Components (v2023.0.2):**
    * Eureka Server, Spring Cloud Gateway, Spring Cloud OpenFeign
    * Spring Cloud LoadBalancer, Resilience4j (Circuit Breaker)
* **Security:** Spring Security (v6.3.1), JWT (JSON Web Tokens), BCrypt Password Encoding
* **Database:** H2 Database (TCP Server Mode for local development)
* **Build Tool:** Apache Maven (v3.x)
* **Libraries:** Lombok, JJWT

## 🚀 Getting Started (Run the Project)

Follow these steps to set up and run the entire microservices system on your local machine.

### Prerequisites

* **Java Development Kit (JDK) 21:** Ensure JDK 21 is installed and configured. Verify with `java -version`.
* **Apache Maven 3.x:** Ensure Maven is installed. Verify with `mvn -v`.
* **Git:** Installed for cloning the repository.
* **A REST Client:** Postman or Insomnia are recommended for API testing.

### Setup and Build

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/](https://github.com/)<your-github-username>/e-learning-microservices.git
    cd e-learning-microservices
    ```
    *(Replace `<your-github-username>` with your actual GitHub username)*

2.  **Build All Microservices:**
    * This step compiles all services and resolves their dependencies.
    * Open your terminal/command prompt at the root of the cloned project (`e-learning-microservices` folder).
    * Execute `mvn clean install` for each service. Alternatively, if your project is set up as a multi-module Maven project, you might run `mvn clean install` from the root. **For safety and clarity, run for each service:**
        ```bash
        cd eurekaserver
        mvn clean install
        cd ../user-service
        mvn clean install
        cd ../course-service
        mvn clean install
        cd ../enrollment-service
        mvn clean install
        cd ../assessment-service
        mvn clean install
        cd ../api-gateway
        mvn clean install
        cd .. # Navigate back to the root project folder
        ```

### Running the Services Locally

**Crucial:** Microservices must be started in a specific order for proper registration with Eureka and inter-service communication. Keep each service running in its own dedicated terminal window or IDE process.

1.  **Start H2 Database Server:**
    * Open a **NEW Terminal/Command Prompt window**.
    * Navigate to *any* service directory that has H2 as a dependency (e.g., `user-service`).
    * Execute the H2 Server command:
    * where i have been used the version h2-2.2.224.jar
        ```bash
        # IMPORTANT: Verify the actual path to your H2 JAR file in your local Maven repository.
        # It's typically found at C:\Users\<YourUsername>\.m2\repository\com\h2database\h2\<version>\h2-<version>.jar
        java -cp "C:\Users\user\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar" org.h2.tools.Server -tcp -web -browser -tcpAllowOthers -ifNotExists
        ```
    * **Keep this terminal window open.** It will display connection details (e.g., `tcp://localhost:9092`). You can access the H2 Console in your browser via `http://localhost:8082` (or the reported web port).
      
      IMPORTANT NOTE STEPS:   
      >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> to avoid the problems when run the services you can :<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        * Open the H2 console after run java -cp "C:\Users\user\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar" org.h2.tools.Server -tcp -web -browser -tcpAllowOthers -ifNotExists 
          in bash by clicking on `http://localhost:8082`.
        * Open it four times in the browser to grant the connection to the database in the services.
        * in field JDBC URL put the name of database that related in the service :
           in the first tab put JDBC URL: jdbc:h2:tcp://localhost/~/data/userdb
           in the second tab put JDBC URL: jdbc:h2:tcp://localhost/~/data/coursedb
           and for the third it will: jdbc:h2:tcp://localhost/~/data/enrollmentdb
           and for the fourh it will: jdbc:h2:tcp://localhost/~/data/assessmentdb
        * On each page, click Connect.
        * then you can run all services without any problems.


2.  **Start Eureka Server:**
    * Open a **NEW Terminal/Command Prompt window**.
    * Navigate to the `eurekaserver` directory.
    * Run the application: `mvn spring-boot:run`
    * **Wait** until you see `Started EurekaserverApplication in X.XXX seconds`.
    * **Verify:** Open `http://localhost:8761` in your browser. You should see the Eureka Dashboard.

3.  **Start User Service:**
    * Open a **NEW Terminal/Command Prompt window**.
    * Navigate to the `user-service` directory.
    * Run the application: `mvn spring-boot:run`
    * **Wait** until you see `Started UserServiceApplication in X.XXX seconds` and messages from `DataLoader` like `Admin user created.`, `Instructor user created.`, etc.
    * **Verify:** Check the Eureka Dashboard (`http://localhost:8761`). `USER-SERVICE` should appear as `UP`.

4.  **Start Course Service:**
    * Open a **NEW Terminal/Command Prompt window**.
    * Navigate to the `course-service` directory.
    * Run the application: `mvn spring-boot:run`
    * **Wait** until it starts.
    * **Verify:** Check the Eureka Dashboard. `COURSE-SERVICE` should appear as `UP`.

5.  **Start Enrollment Service:**
    * Open a **NEW Terminal/Command Prompt window**.
    * Navigate to the `enrollment-service` directory.
    * Run the application: `mvn spring-boot:run`
    * **Wait** until it starts.
    * **Verify:** Check the Eureka Dashboard. `ENROLLMENT-SERVICE` should appear as `UP`.

6.  **Start Assessment Service:**
    * Open a **NEW Terminal/Command Prompt window**.
    * Navigate to the `assessment-service` directory.
    * Run the application: `mvn spring-boot:run`
    * **Wait** until it starts.
    * **Verify:** Check the Eureka Dashboard. `ASSESSMENT-SERVICE` should appear as `UP`.

7.  **Start API Gateway:**
    * Open a **NEW Terminal/Command Prompt window**.
    * Navigate to the `api-gateway` directory.
    * Run the application: `mvn spring-boot:run`
    * **Wait** until it starts.
    * **Verify:** Check the Eureka Dashboard. `API-GATEWAY` should appear as `UP`.

### 🧪 Basic API Testing (Postman Example Sequence)

Use your REST client (Postman/Insomnia) and send requests to `http://localhost:8080` (API Gateway). Remember to always include the `Authorization: Bearer <YOUR_JWT_TOKEN>` header for protected endpoints.

1.  **Get Admin JWT:**
    * `POST http://localhost:8080/users/authenticate`
    * Body: `{"username": "admin", "password": "adminpass"}`
    * **Save the `jwtToken` for `ADMIN_JWT`.**

2.  **Create Instructor (as Admin):**
    * `POST http://localhost:8080/users/admin/add-instructor`
    * Headers: `Authorization: Bearer <ADMIN_JWT>`, `Content-Type: application/json`
    * Body: `{"username": "newInstructorUser", "password": "instructorpass", "email": "new.instructor@example.com"}`
    * **Save the `id` of this new instructor.**

3.  **Get Instructor JWT:**
    * `POST http://localhost:8080/users/authenticate`
    * Body: `{"username": "newInstructorUser", "password": "instructorpass"}`
    * **Save the `jwtToken` for `INSTRUCTOR_JWT`.**

4.  **Create Course (as Instructor):**
    * `POST http://localhost:8080/courses`
    * Headers: `Authorization: Bearer <INSTRUCTOR_JWT>`, `Content-Type: application/json`
    * Body: `{"title": "Distributed Systems Essentials", "description": "Fundamentals of distributed computing.", "instructorId": <instructorId from step 2>}`
    * **Save the `id` of this course.**

5.  **Approve Course (as Admin):**
    * `PUT http://localhost:8080/courses/admin/approve/<courseId from step 4>`
    * Headers: `Authorization: Bearer <ADMIN_JWT>`
    * **Expected:** `200 OK`.

6.  **Get Student JWT:**
    * `POST http://localhost:8080/users/authenticate`
    * Body: `{"username": "student", "password": "studentpass"}`
    * **Expected:** `200 OK`. **Save this JWT.**

7.  **Enroll in Course (as Student):**
    * **URL:** `http://localhost:8080/enrollments/student/enroll/<courseId from step 4>`
    * **Method:** `POST`
    * **Headers:** `Authorization: Bearer <STUDENT_JWT>`
    * **Expected (First Attempt):** `201 Created`.
    * **Expected (Second Attempt):** `400 Bad Request` with message `Student is already enrolled in this course.`.

8.  **Create Assessment (as Instructor):**
    * **URL:** `http://localhost:8080/assessments/instructor/create`
    * **Method:** `POST`
    * **Headers:** `Authorization: Bearer <INSTRUCTOR_JWT>`, `Content-Type: application/json`
    * **Body:** `{"title": "Final Exam - Module 1", "courseId": <courseId from step 4>, "instructorId": <id of newInstructorUser from step 2>, "totalMarks": 10, "passMarks": 7}`
    * **Expected:** `201 Created`. **Save the `assessmentId`.**

9.  **Add Question to Assessment (as Instructor):**
    * **URL:** `http://localhost:8080/assessments/instructor/add-question`
    * **Method:** `POST`
    * **Headers:** `Authorization: Bearer <INSTRUCTOR_JWT>`, `Content-Type: application/json`
    * **Body:** `{"assessmentId": <assessmentId from step 8>, "text": "What is the capital of France?", "optionA": "London", "optionB": "Paris", "optionC": "Rome", "optionD": "Berlin", "correctAnswer": "B"}`
    * **Expected:** `201 Created`. **Save the `id` of this question.**

10. **Submit Assessment (as Student):**
    * **URL:** `http://localhost:8080/assessments/student/submit/<assessmentId from step 8>`
    * **Method:** `POST`
    * **Headers:** `Authorization: Bearer <STUDENT_JWT>`, `Content-Type: application/json`
    * **Body:** `{"<questionId from step 9>": "B"}` (Replace `<questionId from step 9>` with the actual ID)
    * **Expected:** `201 Created` with `score` and `passStatus`.

11. **View Student's Own Enrollments:**
    * **URL:** `http://localhost:8080/enrollments/student/my-enrollments`
    * **Method:** `GET`
    * **Headers:** `Authorization: Bearer <STUDENT_JWT>`
    * **Expected:** `200 OK` with a list of the student's enrollments.

12. **View Student's Own Assessment Results:**
    * **URL:** `http://localhost:8080/assessments/student/my-results`
    * **Method:** `GET`
    * **Headers:** `Authorization: Bearer <STUDENT_JWT>`
    * **Expected:** `200 OK` with a list of the student's assessment results.

13. **View Instructor's Own Assessments:**
    * **URL:** `http://localhost:8080/assessments/instructor/my-assessments`
    * **Method:** `GET`
    * **Headers:** `Authorization: Bearer <INSTRUCTOR_JWT>`
    * **Expected:** `200 OK` with a list of the instructor's assessments.

---
