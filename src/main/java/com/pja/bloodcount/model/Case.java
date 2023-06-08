package com.pja.bloodcount.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.pja.bloodcount.model.enums.AffectedGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "cases")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Case implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int firstMinAge = 0;
    private int firstMaxAge = 0;
    private int secondMinAge = 0;
    private int secondMaxAge = 0;
    @Enumerated(EnumType.STRING)
    private AffectedGender affectedGender;
    private String anemiaType;
    private String diagnosis;

    @OneToMany(
            mappedBy = "aCase",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BloodCountAbnormality> abnormalities = new ArrayList<>();

    public void addAbnormality(BloodCountAbnormality abnormality) {
        if (this.abnormalities == null) {
            this.abnormalities = new ArrayList<>();
        }
        abnormalities.add(abnormality);
        abnormality.setACase(this);
    }

    public void removeAbnormality(BloodCountAbnormality abnormality) {
        abnormalities.remove(abnormality);
        abnormality.setACase(null);
    }
}