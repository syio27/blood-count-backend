package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.response.AnswerResponse;
import com.pja.bloodcount.dto.response.BCAQuestionResponse;
import com.pja.bloodcount.dto.response.MSQuestionResponse;
import com.pja.bloodcount.model.Answer;
import com.pja.bloodcount.model.BCAssessmentQuestion;
import com.pja.bloodcount.model.MSQuestion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QnAMapper {

    public static AnswerResponse mapToAnswerResponseDTO(Answer answer) {
        return AnswerResponse
                .builder()
                .id(answer.getId())
                .text(answer.getText())
                .build();
    }

    public static List<AnswerResponse> mapToAnswerResponseListDTO(List<Answer> answers) {
        return answers.stream()
                .map(answer -> AnswerResponse
                        .builder()
                        .id(answer.getId())
                        .text(answer.getText())
                        .build())
                .toList();
    }

    public static BCAQuestionResponse mapToBCAQuestionResponseDTO(BCAssessmentQuestion question) {
        return BCAQuestionResponse
                .builder()
                .id(question.getId())
                .parameter(question.getParameter())
                .unit(question.getUnit())
                .value(question.getValue())
                .answers(mapToAnswerResponseListDTO(question.getAnswers()))
                .build();
    }

    public static List<BCAQuestionResponse> mapToBCAQResponseListDTO(List<BCAssessmentQuestion> questions) {
        return questions.stream()
                .map(question -> BCAQuestionResponse
                        .builder()
                        .id(question.getId())
                        .parameter(question.getParameter())
                        .unit(question.getUnit())
                        .value(question.getValue())
                        .answers(mapToAnswerResponseListDTO(question.getAnswers()))
                        .build())
                .toList();
    }

    public static MSQuestionResponse mapToMSQuestionResponseDTO(MSQuestion question){
        return MSQuestionResponse
                .builder()
                .id(question.getId())
                .text(question.getText())
                .answers(mapToAnswerResponseListDTO(question.getAnswers()))
                .build();
    }

    public static List<MSQuestionResponse> mapToMSQResponseListDTO(List<MSQuestion> questions) {
        return questions.stream()
                .map(question -> MSQuestionResponse
                        .builder()
                        .id(question.getId())
                        .text(question.getText())
                        .answers(mapToAnswerResponseListDTO(question.getAnswers()))
                        .build())
                .toList();
    }
}
