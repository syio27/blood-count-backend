package com.pja.bloodcount.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.pja.bloodcount.model.enums.Gender;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "patients")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int age;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<BloodCount> bloodCounts = new ArrayList<>();
    @OneToOne(mappedBy = "patient",
            fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Game game;

    public void addBloodCount(BloodCount bloodCount) {
        if (this.bloodCounts == null) {
            this.bloodCounts = new ArrayList<>();
        }
        bloodCounts.add(bloodCount);
        bloodCount.setPatient(this);
    }

    public void removeBloodCount(BloodCount bloodCount) {
        bloodCounts.remove(bloodCount);
        bloodCount.setPatient(null);
    }
}
