package e_learning.enrollment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId; // ID الطالب من UserService
    private Long courseId; // ID الدورة من CourseService
    private String enrollmentDate; // تاريخ الاشتراك
    private String status; // مثلا: PENDING_PAYMENT, ENROLLED, COMPLETED, DROPPED
    private Double paymentAmount; // مبلغ الدفع (وهمي)
    private String paymentStatus; // مثلا: PENDING, PAID, FAILED
}
