package com.develop.management.service;

import com.develop.management.dto.spaceship.*;
import com.develop.management.exception.EntityNotFoundException;
import com.develop.management.exception.InvalidSpaceshipIdException;
import com.develop.management.model.Spaceship;
import com.develop.management.msgbrokers.kafka.KafkaSpaceshipProducer;
import com.develop.management.repository.SpaceshipRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpaceshipServiceImpl implements SpaceshipService {

    private final SpaceshipRepository spaceshipRepository;
    private final ObjectMapper objectMapper;
    private final KafkaSpaceshipProducer kafkaSpaceshipProducer;

    @Cacheable(value = "spaceships", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize + ':sort:' + #pageable.sort.toString()")
    public PageResponseDto<SpaceshipDto> getAllSpaceships(Pageable pageable) {
        Page<Spaceship> spaceships = spaceshipRepository.getAllSpaceships(pageable);
        List<SpaceshipDto> spaceshipDtos = spaceships.getContent().stream()
                .map(this::convertToDto)
                .toList();

        return new PageResponseDto<>(spaceshipDtos, spaceships.getNumber(), spaceships.getSize(),
                spaceships.getTotalElements(), spaceships.getTotalPages());
    }

    @Cacheable(value = "spaceship", key = "#id")
    public Optional<SpaceshipDto> getSpaceshipById(Long id) {
        if (id < 0) {
            throw new InvalidSpaceshipIdException("Spaceship ID cannot be negative, ID: " + id);
        }

        return Optional.ofNullable(spaceshipRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Spaceship with ID " + id + " not found")));
    }

    @Cacheable(value = "spaceships", key = "#name")
    public SpaceshipsDataDto searchSpaceshipsByName(String name) {
        List<SpaceshipDto> dtos = spaceshipRepository.findByNameContaining(name).stream()
                .map(this::convertToDto)
                .toList();

        return SpaceshipsDataDto.builder()
                .spaceships(dtos)
                .build();
    }

    @Transactional
    @CacheEvict(value = "spaceships", allEntries = true)
    public SpaceshipDto createSpaceship(CreateSpaceshipRequestDto spaceshipDto) {
        Spaceship spaceship = convertToEntity(spaceshipDto);
        Spaceship savedSpaceship = spaceshipRepository.save(spaceship);
        SpaceshipDto result = convertToDto(savedSpaceship);
        publishEvent("CREATE", result);
        return result;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "spaceship", key = "#id"),
            @CacheEvict(value = "spaceships", allEntries = true)
    })
    public SpaceshipDto updateSpaceship(Long id, CreateSpaceshipRequestDto spaceshipDto) {
        if (id < 0) {
            throw new InvalidSpaceshipIdException("Spaceship ID cannot be negative, ID: " + id);
        }

        Spaceship existingSpaceship = spaceshipRepository.findById(id)
                .orElseThrow(() -> new InvalidSpaceshipIdException("Spaceship ID not found: " + id));

        updateSpaceshipEntity(existingSpaceship, spaceshipDto);
        Spaceship updatedSpaceship = spaceshipRepository.save(existingSpaceship);

        SpaceshipDto result = convertToDto(updatedSpaceship);
        publishEvent("UPDATE", result);

        return result;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "spaceship", key = "#id"),
            @CacheEvict(value = "spaceships", allEntries = true)
    })
    public void deleteSpaceship(Long id) {
        if (spaceshipRepository.findById(id).isEmpty()) {
            throw new InvalidSpaceshipIdException("Spaceship ID not found: " + id);
        }
        spaceshipRepository.deleteById(id);

        publishEvent("DELETE", id);
    }

    private SpaceshipDto convertToDto(Spaceship spaceship) {
        return objectMapper.convertValue(spaceship, SpaceshipDto.class);
    }

    private Spaceship convertToEntity(CreateSpaceshipRequestDto spaceshipDto) {
        return objectMapper.convertValue(spaceshipDto, Spaceship.class);
    }

    private void updateSpaceshipEntity(Spaceship existingSpaceship, CreateSpaceshipRequestDto spaceshipDto) {
        existingSpaceship.setName(spaceshipDto.getName());
        existingSpaceship.setCrewCapacity(spaceshipDto.getCrewCapacity());
        existingSpaceship.setType(spaceshipDto.getType());
        existingSpaceship.setSeriesOrMovie(spaceshipDto.getSeriesOrMovie());
    }

    private void publishEvent(String eventType, SpaceshipDto spaceshipDto) {
        SpaceshipEventDto event = SpaceshipEventDto.builder()
                .eventType(eventType)
                .spaceshipId(spaceshipDto.getId())
                .spaceshipName(spaceshipDto.getName())
                .crewCapacity(spaceshipDto.getCrewCapacity())
                .type(spaceshipDto.getType())
                .seriesOrMovie(spaceshipDto.getSeriesOrMovie())
                .build();
        kafkaSpaceshipProducer.sendSpaceshipEvent(event);
    }

    private void publishEvent(String eventType, Long id) {
        SpaceshipEventDto event = SpaceshipEventDto.builder()
                .eventType(eventType)
                .spaceshipId(id)
                .build();
        kafkaSpaceshipProducer.sendSpaceshipEvent(event);
    }
}
