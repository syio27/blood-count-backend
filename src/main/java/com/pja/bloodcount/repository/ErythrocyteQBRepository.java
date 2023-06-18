package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.ErythrocyteQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErythrocyteQBRepository extends JpaRepository<ErythrocyteQuestion, Long> {
}
