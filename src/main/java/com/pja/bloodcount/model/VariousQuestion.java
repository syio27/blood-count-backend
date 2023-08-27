package com.pja.bloodcount.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "various_question_base")
public class VariousQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonManagedReference
    @OneToMany(mappedBy = "variousQuestion", cascade = CascadeType.ALL)
    private List<VariousQuestionTranslation> translations;
    @Enumerated(EnumType.STRING)
    private Language language;

    public void addQnA(VariousQuestionTranslation questionTranslation) {
        if (this.translations == null) {
            this.translations = new ArrayList<>();
        }
        translations.add(questionTranslation);
        questionTranslation.setVariousQuestion(this);
    }

    public void removeQnA(VariousQuestionTranslation questionTranslation) {
        translations.remove(questionTranslation);
        questionTranslation.setVariousQuestion(null);
    }
}
