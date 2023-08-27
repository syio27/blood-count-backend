package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.dto.response.SavedUserAnswerResponse;
import com.pja.bloodcount.dto.response.SimpleGameResponse;
import com.pja.bloodcount.model.Game;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GameMapper {

    public static GameResponse mapToResponseDTO(Game game, long remainingTime, List<SavedUserAnswerResponse> savedUserAnswers){
        return GameResponse
                .builder()
                .id(game.getId())
                .startTime(game.getStartTime())
                .endTime(game.getEndTime())
                .estimatedEndTime(game.getEstimatedEndTime())
                .status(game.getStatus())
                .score(game.getScore())
                .currentPage(game.getCurrentPage())
                .testDuration(game.getTestDuration())
                .remainingTime(remainingTime)
                .patient(PatientMapper.mapToResponseDTO(game.getPatient()))
                .gameCaseDetails(game.getCaseDetails())
                .bcAssessmentQuestions(QnAMapper.mapToBCAQResponseListDTO(game.getBcAssessmentQuestions()))
                .msQuestions(QnAMapper.mapToMSQResponseListDTO(game.getMsQuestions()))
                .savedUserAnswers(savedUserAnswers)
                .build();
    }

    public static SimpleGameResponse mapToSimpleResponseDTO(Game game){
        return SimpleGameResponse
                .builder()
                .id(game.getId())
                .startTime(game.getStartTime())
                .endTime(game.getEndTime())
                .estimatedEndTime(game.getEstimatedEndTime())
                .testDuration(game.getTestDuration())
                .status(game.getStatus())
                .score(game.getScore())
                .caseId(game.getCaseDetails().getAnActualCaseId())
                .build();
    }

    public static List<SimpleGameResponse> mapToSimpleResponseListDTO(List<Game> games){
        return games.stream()
                .map(game -> SimpleGameResponse
                        .builder()
                        .id(game.getId())
                        .startTime(game.getStartTime())
                        .endTime(game.getEndTime())
                        .estimatedEndTime(game.getEstimatedEndTime())
                        .testDuration(game.getTestDuration())
                        .status(game.getStatus())
                        .score(game.getScore())
                        .caseId(game.getCaseDetails().getAnActualCaseId())
                        .build())
                .toList();
    }
}