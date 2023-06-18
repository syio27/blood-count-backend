package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.LeukocyteQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeukocyteQBRepository extends JpaRepository<LeukocyteQuestion, Long> {
}
