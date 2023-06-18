package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.model.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public static GameResponse mapToResponseDTO(Game game){
        return GameResponse
                .builder()
                .id(game.getId())
                .startTime(game.getStartTime())
                .endTime(game.getEndTime())
                .estimatedEndTime(game.getEstimatedEndTime())
                .status(game.getStatus())
                .score(game.getScore())
                .testDuration(game.getTestDuration())
                .patient(PatientMapper.mapToResponseDTO(game.getPatient()))
                //.gameCase(CaseMapper.mapToCaseOfGameResponseDTO(game.getGameCase()))
                .gameCaseDetails(game.getCaseDetails())
                .bcAssessmentQuestions(QnAMapper.mapToBCAQResponseListDTO(game.getBcAssessmentQuestions()))
                .msQuestions(QnAMapper.mapToMSQResponseListDTO(game.getMsQuestions()))
                .build();
    }
}