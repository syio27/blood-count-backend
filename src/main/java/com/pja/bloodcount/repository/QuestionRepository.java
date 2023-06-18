package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.BCAssessmentQuestion;
import com.pja.bloodcount.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
