package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.model.BloodCountReference;

import java.util.List;

public interface BCReferenceService {

    List<BloodCountReference> populateTable();
    List<BloodCountReference> fullTableOfBCReference();
}
