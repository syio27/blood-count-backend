package com.pja.bloodcount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedUserAnswerResponse {
    private Long answerId;
    private Long questionId;
}
