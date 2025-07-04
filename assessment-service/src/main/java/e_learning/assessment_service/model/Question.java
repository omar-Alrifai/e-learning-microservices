package e_learning.assessment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long assessmentId; // ID الاختبار الذي تنتمي إليه
    private String text;       // نص السؤال
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer; // الإجابة الصحيحة (A, B, C, D)
}
