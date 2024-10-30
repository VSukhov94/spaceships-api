package com.develop.management.service;

import com.develop.management.dto.spaceship.CreateSpaceshipRequestDto;
import com.develop.management.dto.spaceship.PageResponseDto;
import com.develop.management.dto.spaceship.SpaceshipDto;
import com.develop.management.dto.spaceship.SpaceshipsDataDto;
import com.develop.management.exception.EntityNotFoundException;
import com.develop.management.msgbrokers.kafka.KafkaSpaceshipProducer;
import com.develop.management.model.Spaceship;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.develop.management.exception.InvalidSpaceshipIdException;
import com.develop.management.repository.SpaceshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpaceshipServiceTest {

    @InjectMocks
    private SpaceshipServiceImpl spaceshipService;

    @Mock
    private SpaceshipRepository spaceshipRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaSpaceshipProducer kafkaSpaceshipProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSpaceships_shouldReturnAllSpaceships_whenSpaceshipsExist() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Spaceship> spaceshipList = Arrays.asList(new Spaceship(), new Spaceship());
        Page<Spaceship> spaceshipPage = new PageImpl<>(spaceshipList);

        when(spaceshipRepository.getAllSpaceships(pageable)).thenReturn(spaceshipPage);
        when(objectMapper.convertValue(any(Spaceship.class), eq(SpaceshipDto.class)))
                .thenReturn(new SpaceshipDto());

        PageResponseDto<SpaceshipDto> result = spaceshipService.getAllSpaceships(pageable);

        assertEquals(2, result.getContent().size());
        verify(spaceshipRepository, times(1)).getAllSpaceships(pageable);
    }

    @Test
    void getAllSpaceships_shouldReturnEmptyPage_whenNoSpaceshipsExist() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Spaceship> emptyPage = new PageImpl<>(Collections.emptyList());

        when(spaceshipRepository.getAllSpaceships(pageable)).thenReturn(emptyPage);

        PageResponseDto<SpaceshipDto> result = spaceshipService.getAllSpaceships(pageable);

        assertTrue(result.getContent().isEmpty());
        verify(spaceshipRepository, times(1)).getAllSpaceships(pageable);
    }

    @Test
    void getSpaceshipById_shouldReturnSpaceship_whenIdIsValid() {
        Long spaceshipId = 1L;
        Spaceship spaceship = new Spaceship();
        spaceship.setId(spaceshipId);

        when(spaceshipRepository.findById(spaceshipId)).thenReturn(Optional.of(spaceship));
        when(objectMapper.convertValue(spaceship, SpaceshipDto.class)).thenReturn(new SpaceshipDto());

        Optional<SpaceshipDto> result = spaceshipService.getSpaceshipById(spaceshipId);

        assertTrue(result.isPresent());
        verify(spaceshipRepository, times(1)).findById(spaceshipId);
    }

    @Test
    void getSpaceshipById_shouldThrowException_whenSpaceshipNotFound() {
        Long spaceshipId = 1L;

        when(spaceshipRepository.findById(spaceshipId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            spaceshipService.getSpaceshipById(spaceshipId);
        });

        assertEquals("Spaceship with ID " + spaceshipId + " not found", exception.getMessage());
        verify(spaceshipRepository, times(1)).findById(spaceshipId);
    }

    @Test
    void getSpaceshipById_shouldThrowException_whenIdIsNegative() {
        Long spaceshipId = -1L;

        InvalidSpaceshipIdException exception = assertThrows(InvalidSpaceshipIdException.class, () -> {
            spaceshipService.getSpaceshipById(spaceshipId);
        });

        assertEquals("Spaceship ID cannot be negative, ID: -1", exception.getMessage());
        verify(spaceshipRepository, never()).findById(anyLong());
    }

    @Test
    void searchSpaceshipsByName_shouldReturnMatchingSpaceships_whenMatchesExist() {
        String name = "Millennium";
        List<Spaceship> spaceshipList = Arrays.asList(new Spaceship(), new Spaceship());

        when(spaceshipRepository.findByNameContaining(name)).thenReturn(spaceshipList);
        when(objectMapper.convertValue(any(Spaceship.class), eq(SpaceshipDto.class)))
                .thenReturn(new SpaceshipDto());

        SpaceshipsDataDto result = spaceshipService.searchSpaceshipsByName(name);

        assertEquals(2, result.getSpaceships().size());
        verify(spaceshipRepository, times(1)).findByNameContaining(name);
    }

    @Test
    void searchSpaceshipsByName_shouldReturnEmptyList_whenNoMatchesFound() {
        String name = "Nonexistent Ship";

        when(spaceshipRepository.findByNameContaining(name)).thenReturn(Collections.emptyList());

        SpaceshipsDataDto result = spaceshipService.searchSpaceshipsByName(name);

        assertTrue(result.getSpaceships().isEmpty());
        verify(spaceshipRepository, times(1)).findByNameContaining(name);
    }

    @Test
    void createSpaceship_shouldSaveAndReturnNewSpaceship_whenRequestIsValid() {
        CreateSpaceshipRequestDto spaceshipRequest = new CreateSpaceshipRequestDto();
        Spaceship spaceship = new Spaceship();
        Spaceship savedSpaceship = new Spaceship();
        savedSpaceship.setId(1L);

        SpaceshipDto spaceshipDto = new SpaceshipDto();
        spaceshipDto.setId(1L);

        when(objectMapper.convertValue(spaceshipRequest, Spaceship.class)).thenReturn(spaceship);
        when(spaceshipRepository.save(spaceship)).thenReturn(savedSpaceship);
        when(objectMapper.convertValue(savedSpaceship, SpaceshipDto.class)).thenReturn(spaceshipDto);

        SpaceshipDto result = spaceshipService.createSpaceship(spaceshipRequest);

        assertNotNull(result);
        verify(spaceshipRepository, times(1)).save(spaceship);

        verify(kafkaSpaceshipProducer, times(1)).sendSpaceshipEvent(
                argThat(event -> "CREATE".equals(event.getEventType())
                        && event.getSpaceshipId().equals(1L))
        );
    }

    @Test
    void updateSpaceship_shouldUpdateAndReturnSpaceship_whenIdIsValid() {
        Long spaceshipId = 1L;
        CreateSpaceshipRequestDto spaceshipRequest = CreateSpaceshipRequestDto.builder()
                .name("New Name")
                .type("New Type")
                .crewCapacity(10)
                .seriesOrMovie("New Series")
                .build();

        Spaceship existingSpaceship = new Spaceship();
        existingSpaceship.setId(spaceshipId);

        Spaceship updatedSpaceship = new Spaceship();
        updatedSpaceship.setId(spaceshipId);
        updatedSpaceship.setName(spaceshipRequest.getName());
        updatedSpaceship.setType(spaceshipRequest.getType());
        updatedSpaceship.setCrewCapacity(spaceshipRequest.getCrewCapacity());
        updatedSpaceship.setSeriesOrMovie(spaceshipRequest.getSeriesOrMovie());

        SpaceshipDto updatedSpaceshipDto = new SpaceshipDto();
        updatedSpaceshipDto.setId(spaceshipId);

        when(spaceshipRepository.findById(spaceshipId)).thenReturn(Optional.of(existingSpaceship));
        when(spaceshipRepository.save(existingSpaceship)).thenReturn(updatedSpaceship);
        when(objectMapper.convertValue(updatedSpaceship, SpaceshipDto.class)).thenReturn(updatedSpaceshipDto);

        SpaceshipDto result = spaceshipService.updateSpaceship(spaceshipId, spaceshipRequest);

        assertNotNull(result);
        verify(spaceshipRepository, times(1)).findById(spaceshipId);
        verify(spaceshipRepository, times(1)).save(existingSpaceship);

        verify(kafkaSpaceshipProducer, times(1)).sendSpaceshipEvent(
                argThat(event -> "UPDATE".equals(event.getEventType())
                        && event.getSpaceshipId().equals(spaceshipId))
        );
    }

    @Test
    void updateSpaceship_shouldThrowException_whenSpaceshipNotFound() {
        Long spaceshipId = 1L;
        CreateSpaceshipRequestDto spaceshipRequest = CreateSpaceshipRequestDto.builder()
                .name("New Name")
                .type("New Type")
                .crewCapacity(10)
                .seriesOrMovie("New Series")
                .build();

        when(spaceshipRepository.findById(spaceshipId)).thenReturn(Optional.empty());

        InvalidSpaceshipIdException exception = assertThrows(InvalidSpaceshipIdException.class, () -> {
            spaceshipService.updateSpaceship(spaceshipId, spaceshipRequest);
        });

        assertEquals("Spaceship ID not found: " + spaceshipId, exception.getMessage());
        verify(spaceshipRepository, never()).save(any(Spaceship.class));
    }

    @Test
    void deleteSpaceship_shouldDeleteSpaceship_whenIdIsValid() {
        Long spaceshipId = 1L;

        when(spaceshipRepository.findById(spaceshipId)).thenReturn(Optional.of(new Spaceship()));

        spaceshipService.deleteSpaceship(spaceshipId);

        verify(spaceshipRepository, times(1)).deleteById(spaceshipId);
        verify(kafkaSpaceshipProducer, times(1)).sendSpaceshipEvent(
                argThat(event -> "DELETE".equals(event.getEventType()) && event.getSpaceshipId().equals(spaceshipId))
        );
    }

    @Test
    void deleteSpaceship_shouldThrowException_whenSpaceshipNotFound() {
        Long spaceshipId = 1L;

        when(spaceshipRepository.findById(spaceshipId)).thenReturn(Optional.empty());

        InvalidSpaceshipIdException exception = assertThrows(InvalidSpaceshipIdException.class, () -> {
            spaceshipService.deleteSpaceship(spaceshipId);
        });

        assertEquals("Spaceship ID not found: " + spaceshipId, exception.getMessage());
        verify(spaceshipRepository, never()).deleteById(anyLong());
    }
}
