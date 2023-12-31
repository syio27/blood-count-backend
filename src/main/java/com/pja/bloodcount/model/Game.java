package com.pja.bloodcount.model;

import com.pja.bloodcount.model.enums.Language;
import com.pja.bloodcount.model.enums.Pages;
import com.pja.bloodcount.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private Date startTime;
    private Date endTime;
    private Date estimatedEndTime;
    private int testDuration;
    @Enumerated(EnumType.STRING)
    private Status status;
    private int score = 0;
    @Enumerated(EnumType.STRING)
    private Pages currentPage;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @Enumerated(EnumType.STRING)
    private Language language;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gameCaseDetails_id", referencedColumnName = "id")
    private GameCaseDetails caseDetails;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void addPatient(Patient patient){
        if(patient == null){
            throw new RuntimeException("null patient passed");
        }
        setPatient(patient);
        patient.setGame(this);
    }

    public void removePatient(Patient patient){
        if(patient == null){
            throw new RuntimeException("null patient passed");
        }
        setPatient(null);
        patient.setGame(null);
    }

    public void addQuestion(Question question) {
        validateAndCreate();
        questions.add(question);
        question.setGame(this);
    }

    public void addAllQuestions(List<? extends Question> questions) {
        validateAndCreate();
        this.questions.addAll(questions);
        questions.forEach(question -> question.setGame(this));
    }

    public void removeQuestion(Question question) {
        this.questions.remove(question);
        question.setGame(null);
    }

    public List<BCAssessmentQuestion> getBcAssessmentQuestions() {
        return questions.stream()
                .filter(q -> q instanceof BCAssessmentQuestion)
                .map(q -> (BCAssessmentQuestion) q)
                .collect(Collectors.toList());
    }

    public List<MSQuestion> getMsQuestions() {
        return questions.stream()
                .filter(q -> q instanceof MSQuestion)
                .map(q -> (MSQuestion) q)
                .collect(Collectors.toList());
    }

    public boolean isCompleted() {
        return this.getStatus().equals(Status.COMPLETED);
    }

    public boolean isInProgress() {
        return this.getStatus().equals(Status.IN_PROGRESS);
    }

    public void ifCompletedThenThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
        if(isCompleted()) {
            throw exceptionSupplier.get();
        }
    }

    public void ifInProgressThenThrow(Supplier<? extends RuntimeException> exceptionSupplier) {
        if(isInProgress()) {
            throw exceptionSupplier.get();
        }
    }

    private void validateAndCreate() {
        if (this.questions == null) {
            this.questions = new ArrayList<>();
        }
    }
}
