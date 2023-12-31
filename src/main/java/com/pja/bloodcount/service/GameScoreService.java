package com.pja.bloodcount.service;

import com.pja.bloodcount.exceptions.AnswerNotFoundException;
import com.pja.bloodcount.exceptions.AnswerNotPartException;
import com.pja.bloodcount.exceptions.QuestionNotFoundException;
import com.pja.bloodcount.exceptions.QuestionNotPartException;
import com.pja.bloodcount.model.Question;
import com.pja.bloodcount.model.UserAnswer;
import com.pja.bloodcount.repository.AnswerRepository;
import com.pja.bloodcount.repository.QuestionRepository;
import com.pja.bloodcount.repository.UserAnswerRepository;
import com.pja.bloodcount.service.contract.ScoreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
@AllArgsConstructor
public class GameScoreService implements ScoreService {

    private static final String QUESTION_NOT_PART_EX_MESSAGE = "Question is not part game: %s";
    private static final String ANSWER_NOT_PART_EX_MESSAGE = "Answer is not part of answers set of question: %s";
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    public int score(Long gameId) {
        List<UserAnswer> userAnswers = userAnswerRepository.findByGame_Id(gameId);

        return (int) userAnswers.stream()
                .map(getUserAnswerBooleanFunction(gameId))
                .filter(Boolean::booleanValue)
                .count();
    }

    private Function<UserAnswer, Boolean> getUserAnswerBooleanFunction(Long gameId) {
        return answerRequest -> {
            Question question = questionRepository.findById(answerRequest.getQuestion().getId())
                    .orElseThrow(() -> new QuestionNotFoundException(answerRequest.getQuestion().getId()))
                    .isPartOfGameOrThrow(gameId, () -> new QuestionNotPartException(QUESTION_NOT_PART_EX_MESSAGE.formatted(gameId)));

            answerRepository.findById(answerRequest.getAnswer().getId())
                    .orElseThrow(() -> new AnswerNotFoundException(answerRequest.getAnswer().getId()))
                    .isPartOfQuestionOrThrow(answerRequest.getQuestion().getId(),
                            () -> new AnswerNotPartException(ANSWER_NOT_PART_EX_MESSAGE.formatted(answerRequest.getQuestion().getId())));

            return Objects.equals(question.getCorrectAnswerId(), answerRequest.getAnswer().getId());
        };
    }
}
