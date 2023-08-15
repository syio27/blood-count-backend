package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.GameCaseDetails;
import com.pja.bloodcount.model.enums.Pages;
import com.pja.bloodcount.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {

    private Long id;
    private Date startTime;
    private Date endTime;
    private Date estimatedEndTime;
    private long remainingTime;
    private int testDuration;
    private Status status;
    private int score;
    private Pages currentPage;
    private PatientResponse patient;
    private GameCaseDetails gameCaseDetails;
    private List<BCAQuestionResponse> bcAssessmentQuestions = new ArrayList<>();
    private List<MSQuestionResponse> msQuestions = new ArrayList<>();
}
