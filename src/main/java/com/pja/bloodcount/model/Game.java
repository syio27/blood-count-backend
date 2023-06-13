package com.pja.bloodcount.model;

import com.pja.bloodcount.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

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
}
