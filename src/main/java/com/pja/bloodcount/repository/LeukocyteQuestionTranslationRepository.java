package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.LeukocyteQuestionTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeukocyteQuestionTranslationRepository extends JpaRepository<LeukocyteQuestionTranslation, Long> {
}
