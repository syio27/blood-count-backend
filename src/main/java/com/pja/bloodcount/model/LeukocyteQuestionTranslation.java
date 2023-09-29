package com.pja.bloodcount.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "leukocyte_question_translation")
public class LeukocyteQuestionTranslation implements QuestionTranslationBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leukocyte_question_id")
    private LeukocyteQuestion leukocyteQuestion;
    private String text;
    private String answer;
}
