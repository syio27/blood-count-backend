package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BCAQuestionResponse {
    private Long id;
    private String parameter;
    private String unit;
    private Double value;
    private List<AnswerResponse> answers = new ArrayList<>();
}
