package com.pja.bloodcount.dto.request;

import com.pja.bloodcount.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMSQQuestionRequest {
    private String text;
    private String answer;
}
