package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.repository.AnswerRepository;
import com.pja.bloodcount.repository.BCAssessmentQuestionRepository;
import com.pja.bloodcount.repository.GameRepository;
import com.pja.bloodcount.validation.PatientValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@AllArgsConstructor
public class QnAService {

    private final BCAssessmentQuestionRepository bcaQuestionRepository;
    private final AnswerRepository answerRepository;
    private final GameRepository gameRepository;
    private final PatientValidator patientValidator;

    public List<BCAssessmentQuestion>  createQnAForBCAssessment(Long gameId){
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if(optionalGame.isEmpty()){
            // TODO: change to Game related Exception class
            throw new RuntimeException("game is not found");
        }
        Game game = optionalGame.get();
        Patient patient = patientValidator.validateIfExistsAndGet(game.getPatient().getId());
        List<BloodCount> bloodCountList = patient.getBloodCounts();
        List<BCAssessmentQuestion> questionList = new ArrayList<>();
        bloodCountList.forEach(bloodCount -> {
            if(isForAssessment(bloodCount.getParameter(), bloodCount.getUnit())){
               Answer answer1 = Answer
                       .builder()
                       .text("INCREASED")
                       .build();
               Answer answer2 = Answer
                        .builder()
                        .text("NORMAL")
                        .build();
               Answer answer3 = Answer
                        .builder()
                        .text("DECREASED")
                        .build();

               //answerRepository.saveAll(List.of(answer1, answer2, answer3));

               BCAssessmentQuestion question = BCAssessmentQuestion
                       .builder()
                       .parameter(bloodCount.getParameter())
                       .unit(bloodCount.getUnit())
                       .value(bloodCount.getValue())
                       .build();
               question.addAnswer(answer1);
               question.addAnswer(answer2);
               question.addAnswer(answer3);
               bcaQuestionRepository.save(question);
               List<Answer> answers = question.getAnswers();
               answers.forEach(answer -> {
                   if(bloodCount.getLevelType().name().equals(answer.getText())){
                       question.setCorrectAnswerId(answer.getId());
                   }
               });
               questionList.add(question);
            }
        });
        bcaQuestionRepository.saveAll(questionList);
        return questionList;
    }

    public int score(List<AnswerRequest> answerRequestList){
        AtomicInteger score = new AtomicInteger(0);
        answerRequestList.forEach(answerRequest -> {
            Optional<BCAssessmentQuestion> optionalQuestion = bcaQuestionRepository.findById(answerRequest.getQuestionId());
            if(optionalQuestion.isEmpty()){
                throw new RuntimeException("Question is not found");
            }
            BCAssessmentQuestion question = optionalQuestion.get();

            Optional<Answer> optionalAnswer = answerRepository.findById(answerRequest.getAnswerId());
            if(optionalAnswer.isEmpty()){
                throw new RuntimeException("Answer is not found");
            }
            Answer answer = optionalAnswer.get();
            log.info("Answer's question id: {}", answer.getQuestion().getId());
            log.info("question id from request: {}",answerRequest.getQuestionId());
            if(!Objects.equals(answer.getQuestion().getId(), answerRequest.getQuestionId())){
                throw new RuntimeException("Answer is not part of answers set of question: " + answerRequest.getQuestionId());
            }
            if(Objects.equals(question.getCorrectAnswerId(), answerRequest.getAnswerId())){
                score.getAndIncrement();
            }
        });
        return score.get();
    }

    private boolean isForAssessment(String parameter, String unit) {
        HashMap<String, String> bloodCountMap = new HashMap<>();
        bloodCountMap.put("WBC", "10^9/L");
        bloodCountMap.put("RBC", "10^12/L");
        bloodCountMap.put("HGB", "g/dl");
        bloodCountMap.put("HCT", "%");
        bloodCountMap.put("MCV", "fl");
        bloodCountMap.put("MCH", "pg");
        bloodCountMap.put("MCHC", "g/dl");
        bloodCountMap.put("PLT", "10^9/L");

        return bloodCountMap.containsKey(parameter) && bloodCountMap.containsValue(unit);
    }
}
