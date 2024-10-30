package com.develop.management.dto.spaceship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceshipEventDto {

    private String eventType; //"CREATE", "UPDATE", "DELETE"

    private Long spaceshipId;

    private String spaceshipName;

    private int crewCapacity;

    private String type;

    private String seriesOrMovie;
}
