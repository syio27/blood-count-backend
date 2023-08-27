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
@Table(name = "leukocyte_question_base")
public class LeukocyteQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonManagedReference
    @OneToMany(mappedBy = "leukocyteQuestion", cascade = CascadeType.ALL)
    private List<LeukocyteQuestionTranslation> translations;
    @Enumerated(EnumType.STRING)
    private Language language;

    public void addQnA(LeukocyteQuestionTranslation questionTranslation) {
        if (this.translations == null) {
            this.translations = new ArrayList<>();
        }
        translations.add(questionTranslation);
        questionTranslation.setLeukocyteQuestion(this);
    }

    public void removeQnA(LeukocyteQuestionTranslation questionTranslation) {
        translations.remove(questionTranslation);
        questionTranslation.setLeukocyteQuestion(null);
    }
}
