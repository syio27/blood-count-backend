package com.pja.bloodcount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameInProgress {
    private Long gameId;
    private boolean inProgress;
    private Integer numberOfQuestions;
    private Integer numberOfAnsweredQuestions;
}
