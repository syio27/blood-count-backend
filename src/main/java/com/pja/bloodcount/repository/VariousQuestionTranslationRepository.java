package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.VariousQuestionTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariousQuestionTranslationRepository extends JpaRepository<VariousQuestionTranslation, Long> {
}
