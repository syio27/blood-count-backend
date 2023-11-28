package com.pja.bloodcount.service;

import com.pja.bloodcount.exceptions.AnswerNotFoundException;
import com.pja.bloodcount.exceptions.AnswerNotPartException;
import com.pja.bloodcount.exceptions.QuestionNotFoundException;
import com.pja.bloodcount.exceptions.QuestionNotPartException;
import com.pja.bloodcount.model.Answer;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@AllArgsConstructor
public class GameScoreService implements ScoreService {

    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    public int score(Long gameId) {
        AtomicInteger score = new AtomicInteger(0);
        List<UserAnswer> userAnswers = userAnswerRepository.findByGame_Id(gameId);

        userAnswers.forEach(answerRequest -> {
            Question question = questionRepository.findById(answerRequest.getQuestion().getId())
                    .orElseThrow(() -> new QuestionNotFoundException(answerRequest.getQuestion().getId()));
            if (!Objects.equals(question.getGame().getId(), gameId)) {
                throw new QuestionNotPartException("Question is not part game: " + gameId);
            }
            Answer answer = answerRepository.findById(answerRequest.getAnswer().getId())
                    .orElseThrow(() -> new AnswerNotFoundException(answerRequest.getAnswer().getId()));
            if (!Objects.equals(answer.getQuestion().getId(), answerRequest.getQuestion().getId())) {
                throw new AnswerNotPartException("Answer is not part of answers set of question: " + answerRequest.getQuestion().getId());
            }

            if (Objects.equals(question.getCorrectAnswerId(), answerRequest.getAnswer().getId())) {
                score.getAndIncrement();
            }
        });

        return score.get();
    }
}
