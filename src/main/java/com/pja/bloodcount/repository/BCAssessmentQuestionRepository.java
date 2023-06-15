package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.BCAssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BCAssessmentQuestionRepository extends JpaRepository<BCAssessmentQuestion, Long> {
}
