package com.pja.bloodcount.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.pja.bloodcount.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Reference Entity to Case
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "game_case_details")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class GameCaseDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long anActualCaseId;
    private String anemiaType;
    @Column(length = 1000)
    private String diagnosis;
    @Column(length = 1000)
    private String details;
    private String hr;
    private String rr;
    @Column(length = 1000)
    private String description;
    @Column(length = 1000)
    private String infoCom;
    private String caseName;
    @Enumerated(EnumType.STRING)
    private Language language;
    private String bodyMass;
    private String height;
    private String bmi;
}
