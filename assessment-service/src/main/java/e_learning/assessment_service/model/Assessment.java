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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Long courseId;     // ID الدورة التي ينتمي إليها الاختبار
    private Long instructorId; // ID المدرب الذي أنشأ الاختبار
    private Integer totalMarks; // العلامة الكلية للاختبار
    private Integer passMarks;  // علامة النجاح
}
