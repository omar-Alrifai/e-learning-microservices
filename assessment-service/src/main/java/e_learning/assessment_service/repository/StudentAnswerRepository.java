package e_learning.assessment_service.repository;

import e_learning.assessment_service.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByStudentIdAndAssessmentId(Long studentId, Long assessmentId);
    List<StudentAnswer> findByQuestionId(Long questionId);
    @Modifying
    @Transactional
    @Query("DELETE FROM StudentResult sr WHERE sr.assessmentId = ?1")
    void deleteByAssessmentId(Long assessmentId);

}
