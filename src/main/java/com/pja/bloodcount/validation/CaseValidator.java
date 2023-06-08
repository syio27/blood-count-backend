package com.pja.bloodcount.validation;

import com.pja.bloodcount.exceptions.CaseNotFoundException;
import com.pja.bloodcount.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class CaseValidator extends EntityValidator<Case, Long>{
    public CaseValidator(JpaRepository<Case, Long> repository) {
        super(repository);
    }

    @Override
    protected RuntimeException getCollectionIsEmptyException() {
        return null;
    }

    @Override
    protected RuntimeException getNotFoundException(Long id) {
        return new CaseNotFoundException(id);
    }
}
