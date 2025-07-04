package e_learning.assessment_service.repository;


import e_learning.assessment_service.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByCourseId(Long courseId);
    List<Assessment> findByInstructorId(Long instructorId);
}
