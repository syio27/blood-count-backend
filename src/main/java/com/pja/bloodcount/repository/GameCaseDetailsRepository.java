package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.GameCaseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCaseDetailsRepository extends JpaRepository<GameCaseDetails, Long> {
}
