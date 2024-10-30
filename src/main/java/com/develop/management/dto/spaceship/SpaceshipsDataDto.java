package com.develop.management.dto.spaceship;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SpaceshipsDataDto {

    private List<SpaceshipDto> spaceships;
}
