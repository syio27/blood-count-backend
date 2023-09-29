package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.model.BCAssessmentQuestion;
import com.pja.bloodcount.model.MSQuestion;
import com.pja.bloodcount.model.enums.Language;

import java.util.List;

public interface QnAService {
    List<BCAssessmentQuestion> createQnAForBCAssessment(Long gameId);
    List<MSQuestion> createMSQuestions(Long gameId, Language language);
    List<MSQuestion> createTrueFalseMSQuestions(Long gameId, Language language);
}
