package com.pja.bloodcount.validation;

import com.pja.bloodcount.exceptions.PatientNotFoundException;
import com.pja.bloodcount.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class PatientValidator extends EntityValidator<Patient, Long>{
    public PatientValidator(JpaRepository<Patient, Long> repository) {
        super(repository);
    }

    @Override
    protected RuntimeException getCollectionIsEmptyException() {
        return null;
    }

    @Override
    protected RuntimeException getNotFoundException(Long id) {
        return new PatientNotFoundException(id);
    }
}
