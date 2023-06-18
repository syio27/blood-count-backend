package com.pja.bloodcount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MSQuestionResponse {
    private Long id;
    private Long correctAnswerId;
    private String text;
    private List<AnswerResponse> answers = new ArrayList<>();
}
