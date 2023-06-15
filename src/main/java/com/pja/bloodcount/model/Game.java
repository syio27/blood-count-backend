package com.pja.bloodcount.model;

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
    // record time when user completes game
    private Date endTime;
    // time when test auto closes
    private Date estimatedEndTime;
    private int testDuration; // in minutes
    @Enumerated(EnumType.STRING)
    private Status status;
    private int score = 0;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @OneToOne
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    private Case gameCase;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BCAssessmentQuestion> bcAssessmentQuestions = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void addPatient(Patient patient){
        if(patient == null){
            throw new RuntimeException("null patient");
        }
        setPatient(patient);
        patient.setGame(this);
    }

    public void removePatient(Patient patient){
        if(patient == null){
            throw new RuntimeException("null patient");
        }
        setPatient(null);
        patient.setGame(null);
    }

    public void addBCAssessmentQuestion(BCAssessmentQuestion question) {
        if (this.bcAssessmentQuestions == null) {
            this.bcAssessmentQuestions = new ArrayList<>();
        }
        bcAssessmentQuestions.add(question);
        question.setGame(this);
    }

    public void removeBCAssessmentQuestion(BCAssessmentQuestion question) {
        bcAssessmentQuestions.remove(question);
        question.setGame(null);
    }
}
