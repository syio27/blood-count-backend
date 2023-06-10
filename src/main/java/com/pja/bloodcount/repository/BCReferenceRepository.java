package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.BloodCountReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BCReferenceRepository extends JpaRepository<BloodCountReference, Long> {
}
