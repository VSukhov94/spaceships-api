package com.develop.management.dto.spaceship;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpaceshipRequestDto {

    @NotNull
    @Size(min = 1)
    private String name;

    @NotEmpty
    private String seriesOrMovie;

    @NotEmpty
    private String type;

    private int crewCapacity;
}
