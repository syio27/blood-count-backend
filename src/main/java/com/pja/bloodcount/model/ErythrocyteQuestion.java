package com.pja.bloodcount.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pja.bloodcount.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "erythrocyte_question_base")
public class ErythrocyteQuestion implements QuestionBase<ErythrocyteQuestionTranslation> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonManagedReference
    @OneToMany(mappedBy = "erythrocyteQuestion", cascade = CascadeType.ALL)
    private List<ErythrocyteQuestionTranslation> translations;
    @Enumerated(EnumType.STRING)
    private Language language;

    public void addQnA(ErythrocyteQuestionTranslation questionTranslation) {
        if (this.translations == null) {
            this.translations = new ArrayList<>();
        }
        translations.add(questionTranslation);
        questionTranslation.setErythrocyteQuestion(this);
    }

    public void removeQnA(ErythrocyteQuestionTranslation questionTranslation) {
        translations.remove(questionTranslation);
        questionTranslation.setErythrocyteQuestion(null);
    }
}
