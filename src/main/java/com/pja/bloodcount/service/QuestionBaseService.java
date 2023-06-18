package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.CreateMSQQuestionRequest;
import com.pja.bloodcount.model.ErythrocyteQuestion;
import com.pja.bloodcount.model.LeukocyteQuestion;
import com.pja.bloodcount.model.VariousQuestion;
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

    public List<ErythrocyteQuestion> createErythrocytes(List<CreateMSQQuestionRequest> requests){
        List<ErythrocyteQuestion> questions = new ArrayList<>();
        requests.forEach(request -> {
            ErythrocyteQuestion question = ErythrocyteQuestion
                    .builder()
                    .text(request.getText())
                    .answer(request.getAnswer())
                    .build();
            questions.add(question);
        });
        erythrocyteQBRepository.saveAll(questions);
        return questions;
    }

    public List<LeukocyteQuestion> createLeukocyte(List<CreateMSQQuestionRequest> requests){
        List<LeukocyteQuestion> questions = new ArrayList<>();
        requests.forEach(request -> {
            LeukocyteQuestion question = LeukocyteQuestion
                    .builder()
                    .text(request.getText())
                    .answer(request.getAnswer())
                    .build();
            questions.add(question);
        });
        leukocyteQBRepository.saveAll(questions);
        return questions;
    }

    public List<VariousQuestion> createVariousQ(List<CreateMSQQuestionRequest> requests){
        List<VariousQuestion> questions = new ArrayList<>();
        requests.forEach(request -> {
            VariousQuestion question = VariousQuestion
                    .builder()
                    .text(request.getText())
                    .answer(request.getAnswer())
                    .build();
            questions.add(question);
        });
        variousQBRepository.saveAll(questions);
        return questions;
    }
}
