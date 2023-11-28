package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.response.*;
import com.pja.bloodcount.model.enums.Language;

import java.util.List;
import java.util.UUID;

public interface GameService {

    void startGameSession(Long caseId, UUID userId, Language language);
    SimpleGameResponse completeGame(Long gameId);
    void queueCompleteGame(Long gameId);
    void saveSelectedAnswers(Long gameId, List<AnswerRequest> answerRequestList);
    GameCurrentSessionState next(UUID userId, Long gameId, List<AnswerRequest> answerRequestList);
    List<SimpleGameResponse> getAllCompletedGamesOfUser(UUID userId);
    GameResponse getInProgressGame(Long gameId, UUID userId);
    GameInProgress hasGameInProgress(UUID userId);
}
