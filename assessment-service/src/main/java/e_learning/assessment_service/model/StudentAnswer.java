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
@Table(name = "student_answers")
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;     // ID الطالب الذي أجرى الاختبار
    private Long assessmentId;  // ID الاختبار الذي تم الإجابة عليه
    private Long questionId;    // ID السؤال الذي تم الإجابة عليه
    private String studentChoice; // اختيار الطالب (A, B, C, D)
}
