package com.pja.bloodcount.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long correctAnswerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @EqualsAndHashCode.Exclude
    private Game game;
}
