package com.pja.bloodcount.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@DiscriminatorValue(value = "BC_ASSESSMENT")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class BCAssessmentQuestion extends Question{
    private String parameter;
    private String unit;
    @Column(name = "`value`")
    private Double value;
}
