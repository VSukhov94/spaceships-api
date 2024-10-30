package com.develop.management.service;

import com.develop.management.dto.spaceship.CreateSpaceshipRequestDto;
import com.develop.management.dto.spaceship.PageResponseDto;
import com.develop.management.dto.spaceship.SpaceshipDto;
import com.develop.management.dto.spaceship.SpaceshipsDataDto;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SpaceshipService {

    PageResponseDto<SpaceshipDto> getAllSpaceships(Pageable pageable);

    Optional<SpaceshipDto> getSpaceshipById(Long id);

    SpaceshipsDataDto searchSpaceshipsByName(String name);

    SpaceshipDto createSpaceship(CreateSpaceshipRequestDto spaceshipDto);

    SpaceshipDto updateSpaceship(Long id, CreateSpaceshipRequestDto spaceshipDto);

    void deleteSpaceship(Long id);

}
