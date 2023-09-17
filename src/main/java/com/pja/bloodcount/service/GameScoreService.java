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

        log.info("Started validation of QnA of user");
        userAnswers.forEach(answerRequest -> {
            Optional<Question> optionalQuestion = questionRepository.findById(answerRequest.getQuestion().getId());
            if (optionalQuestion.isEmpty()) {
                throw new QuestionNotFoundException(answerRequest.getQuestion().getId());
            }
            Question question = optionalQuestion.get();
            if (!Objects.equals(question.getGame().getId(), gameId)) {
                throw new QuestionNotPartException("Question is not part game: " + gameId);
            }
            Optional<Answer> optionalAnswer = answerRepository.findById(answerRequest.getAnswer().getId());
            if (optionalAnswer.isEmpty()) {
                throw new AnswerNotFoundException(answerRequest.getAnswer().getId());
            }
            Answer answer = optionalAnswer.get();
            if (!Objects.equals(answer.getQuestion().getId(), answerRequest.getQuestion().getId())) {
                throw new AnswerNotPartException("Answer is not part of answers set of question: " + answerRequest.getQuestion().getId());
            }

            log.info("Started scoring");

            log.info("Correct answer id of question: {}, is {}, and user selected answer with id {}", question.getId(), question.getCorrectAnswerId(), answerRequest.getAnswer().getId());
            log.info("Score was before incrementing: {}", score);
            if (Objects.equals(question.getCorrectAnswerId(), answerRequest.getAnswer().getId())) {

                score.getAndIncrement();
                log.info("Score after incrementing: {}", score);
            }
        });
        log.info("Scoring end");
        return score.get();
    }
}
