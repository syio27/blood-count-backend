package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public static GameResponse mapToResponseDTO(Game game, Patient patient){
        return GameResponse
                .builder()
                .id(game.getId())
                .startTime(game.getStartTime())
                .endTime(game.getEndTime())
                .estimatedEndTime(game.getEstimatedEndTime())
                .status(game.getStatus())
                .testDuration(game.getTestDuration())
                .patient(PatientMapper.mapToResponseDTO(patient))
                .gameCase(CaseMapper.mapToCaseOfGameResponseDTO(game.getGameCase()))
                .build();
    }
}