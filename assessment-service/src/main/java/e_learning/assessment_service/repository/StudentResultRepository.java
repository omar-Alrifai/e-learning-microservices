package e_learning.assessment_service.repository;

import e_learning.assessment_service.model.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentResultRepository extends JpaRepository<StudentResult, Long> {
    Optional<StudentResult> findByStudentIdAndAssessmentId(Long studentId, Long assessmentId);
    List<StudentResult> findByStudentId(Long studentId);
    List<StudentResult> findByAssessmentId(Long assessmentId);
    @Modifying
    @Transactional
    @Query("DELETE FROM StudentAnswer sa WHERE sa.assessmentId = ?1")
    void deleteByAssessmentId(Long assessmentId);

}