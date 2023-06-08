package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.BloodCountAbnormality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbnormalityRepository extends JpaRepository<BloodCountAbnormality, Long> {
}
