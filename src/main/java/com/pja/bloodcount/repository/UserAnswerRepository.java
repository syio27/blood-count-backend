package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.model.Question;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    List<UserAnswer> findByUser_IdAndGame_Id(UUID userId, Long gameId);
    void deleteUserAnswerByUser(User user);
    Optional<UserAnswer> findByQuestionAndGame(Question question, Game game);
    List<UserAnswer> findByGame_Id(Long gameId);
}
