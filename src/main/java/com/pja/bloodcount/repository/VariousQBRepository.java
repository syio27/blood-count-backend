package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.VariousQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariousQBRepository extends JpaRepository<VariousQuestion, Long> {
}
