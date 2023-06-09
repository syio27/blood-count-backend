package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    @Query("SELECT c FROM Case c LEFT JOIN FETCH c.abnormalities WHERE c.id = :id")
    Optional<Case> findCaseWithAbnormalities(@Param("id") Long id);

    @Query("SELECT c FROM Case c LEFT JOIN FETCH c.abnormalities")
    List<Case> findAllCasesWithAbnormalities();
}
