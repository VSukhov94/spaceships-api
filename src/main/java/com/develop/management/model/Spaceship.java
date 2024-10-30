package com.develop.management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "spaceships")
public class Spaceship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String seriesOrMovie;

    @Column(nullable = false)
    private String type;

    private int crewCapacity;

    @Column(nullable = false)
    private boolean isDeleted = false;

}
