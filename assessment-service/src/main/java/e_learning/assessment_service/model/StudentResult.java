package e_learning.assessment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_results")
public class StudentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private Long assessmentId;
    private Integer score;         // علامة الطالب في هذا الاختبار
    private String passStatus;     // PASSED, FAILED
    private String submissionDate; // تاريخ تقديم الاختبار
}
