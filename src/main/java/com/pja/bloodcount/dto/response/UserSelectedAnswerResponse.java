package com.pja.bloodcount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSelectedAnswerResponse {
    private Long id;
    private String answer;
    private String questionText;
}
