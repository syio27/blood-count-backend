package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.enums.Language;
import com.pja.bloodcount.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameToExport {
    private Long id;
    private Language language;
    private String userEmail;
    private String userGroup;
    private Date startTime;
    private Date endTime;
    private Date estimatedEndTime;
    private int testDuration;
    private Status status;
    private int score;
    private String patientInfo;
    private String playedCaseId;
    private String caseInfo;
}
