package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    List<UserAnswer> findByUser_IdAndGame_Id(UUID userId, Long gameId);
}
