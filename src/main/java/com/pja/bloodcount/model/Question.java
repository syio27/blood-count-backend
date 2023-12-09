package com.pja.bloodcount.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuperBuilder(toBuilder = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "QUESTION_TYPE",
        discriminatorType = DiscriminatorType.STRING
)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long correctAnswerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @EqualsAndHashCode.Exclude
    private Game game;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    public void addAnswer(Answer answer) {
        validateAndCreate();
        this.answers.add(answer);
        answer.setQuestion(this);
    }

    public void addAllAnswers(List<Answer> answers) {
        validateAndCreate();
        this.answers.addAll(answers);
        answers.forEach(answer -> answer.setQuestion(this));
    }

    public void addAllAnswers(Answer... answers) {
        validateAndCreate();
        this.answers.addAll(Arrays.asList(answers));
        Arrays.asList(answers).forEach(answer -> answer.setQuestion(this));
    }

    public void removeAnswer(Answer answer) {
        this.answers.remove(answer);
        answer.setQuestion(null);
    }

    public void removeAllAnswers() {
        this.answers.forEach(answer -> answer.setQuestion(null));
        this.answers.clear();
    }

    private void validateAndCreate() {
        if (this.answers == null) {
            this.answers = new ArrayList<>();
        }
    }
}
