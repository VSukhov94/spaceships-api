package com.develop.management.dto.spaceship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceshipDto {

    private Long id;

    private String name;

    private String seriesOrMovie;

    private String type;

    private int crewCapacity;
}
