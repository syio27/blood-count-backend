package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.model.enums.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Override
    @EntityGraph(attributePaths = {"patient", "caseDetails", "questions"})
    Optional<Game> findById(Long id);
    List<Game> findByUser_Id(UUID uuid);
    @Query("SELECT g FROM Game g WHERE g.id = :id AND g.user.id = :uuid")
    Optional<Game> findByIdAndUserId(Long id, UUID uuid);
    List<Game> findAllByEstimatedEndTimeBeforeAndStatus(Date date, Status status);
}
