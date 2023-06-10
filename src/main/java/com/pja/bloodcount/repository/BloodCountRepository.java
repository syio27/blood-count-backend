package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.BloodCount;
import com.pja.bloodcount.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodCountRepository extends JpaRepository<BloodCount, Long> {

    BloodCount findByParameterAndUnitAndPatient(String parameter, String unit, Patient patient);
}
