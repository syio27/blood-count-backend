package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.BCAssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BCAssessmentQuestionRepository extends QuestionRepository {
}