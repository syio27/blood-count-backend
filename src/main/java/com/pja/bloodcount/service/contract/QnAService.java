package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.model.BCAssessmentQuestion;
import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.model.MSQuestion;
import com.pja.bloodcount.model.enums.Language;

import java.util.List;

public interface QnAService {
    List<BCAssessmentQuestion> createQnAForBCAssessment(Game game);
    List<MSQuestion> createMSQuestions(Game game, Language language);
    List<MSQuestion> createTrueFalseMSQuestions(Language language);
}
