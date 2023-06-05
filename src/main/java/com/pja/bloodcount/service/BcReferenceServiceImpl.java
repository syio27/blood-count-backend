package com.pja.bloodcount.service;

import com.pja.bloodcount.exceptions.ReferenceTableException;
import com.pja.bloodcount.model.BloodCountReference;
import com.pja.bloodcount.repository.BCReferenceRepository;
import com.pja.bloodcount.service.contract.BCReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BcReferenceServiceImpl implements BCReferenceService {

    private final BCReferenceRepository repository;
    private static final int TABLE_SIZE = 20;

    @Override
    public List<BloodCountReference> fullTableOfBCReference() {
        List<BloodCountReference> referenceTable = repository.findAll();
        if(referenceTable.isEmpty() || referenceTable.size() != TABLE_SIZE){
            throw new ReferenceTableException("Reference Table size is not enough");
        }
        return referenceTable;
    }
}
