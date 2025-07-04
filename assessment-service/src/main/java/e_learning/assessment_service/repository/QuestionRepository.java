package e_learning.assessment_service.repository;

import e_learning.assessment_service.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByAssessmentId(Long assessmentId);
    // ✅ إضافة هذه الطريقة
    @Modifying
    @Transactional
    @Query("DELETE FROM Question q WHERE q.assessmentId = ?1")
    void deleteByAssessmentId(Long assessmentId);
}
