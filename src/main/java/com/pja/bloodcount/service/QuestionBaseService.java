package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.CreateMSQQuestionRequest;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.Language;
import com.pja.bloodcount.repository.ErythrocyteQBRepository;
import com.pja.bloodcount.repository.LeukocyteQBRepository;
import com.pja.bloodcount.repository.VariousQBRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class QuestionBaseService {

    private final VariousQBRepository variousQBRepository;
    private final ErythrocyteQBRepository erythrocyteQBRepository;
    private final LeukocyteQBRepository leukocyteQBRepository;

    public List<ErythrocyteQuestionTranslation> createErythrocytes(List<CreateMSQQuestionRequest> requests, Language language){
        ErythrocyteQuestion erythrocyteQuestion = ErythrocyteQuestion
                .builder()
                .language(language)
                .translations(new ArrayList<>())
                .build();

        requests.forEach(request -> {
            ErythrocyteQuestionTranslation question = ErythrocyteQuestionTranslation
                    .builder()
                    .text(request.getText())
                    .answer(request.getAnswer())
                    .build();
            erythrocyteQuestion.addQnA(question);
        });
        erythrocyteQBRepository.save(erythrocyteQuestion);
        return erythrocyteQuestion.getTranslations();
    }

    public List<LeukocyteQuestionTranslation> createLeukocyte(List<CreateMSQQuestionRequest> requests, Language language){
        LeukocyteQuestion leukocyteQuestion = LeukocyteQuestion
                .builder()
                .language(language)
                .translations(new ArrayList<>())
                .build();

        requests.forEach(request -> {
            LeukocyteQuestionTranslation question = LeukocyteQuestionTranslation
                    .builder()
                    .text(request.getText())
                    .answer(request.getAnswer())
                    .build();
            leukocyteQuestion.addQnA(question);
        });
        leukocyteQBRepository.save(leukocyteQuestion);
        return leukocyteQuestion.getTranslations();
    }

    public List<VariousQuestionTranslation> createVariousQ(List<CreateMSQQuestionRequest> requests, Language language){
        VariousQuestion variousQuestion = VariousQuestion
                .builder()
                .language(language)
                .translations(new ArrayList<>())
                .build();

        requests.forEach(request -> {
            VariousQuestionTranslation question = VariousQuestionTranslation
                    .builder()
                    .text(request.getText())
                    .answer(request.getAnswer())
                    .build();
            variousQuestion.addQnA(question);
        });
        variousQBRepository.save(variousQuestion);
        return variousQuestion.getTranslations();
    }
}
