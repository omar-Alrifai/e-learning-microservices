package e_learning.assessment_service.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String enrollmentDate;
    private String status;
    private Double paymentAmount;
    private String paymentStatus;
}
