package com.pja.bloodcount.dto.response;

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
public class GameCurrentSessionState {

    private Long gameId;
    private Date estimatedEndTime;
    private Status status;
    private Pages currentPage;
    private List<SavedUserAnswerResponse> savedUserAnswers = new ArrayList<>();;
}