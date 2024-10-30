package com.develop.management.controller;

import com.develop.management.dto.spaceship.CreateSpaceshipRequestDto;
import com.develop.management.dto.spaceship.PageResponseDto;
import com.develop.management.dto.spaceship.SpaceshipDto;
import com.develop.management.dto.spaceship.SpaceshipsDataDto;
import com.develop.management.exception.EntityNotFoundException;
import com.develop.management.security.JwtUtils;
import com.develop.management.service.SpaceshipServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.develop.management.controller.TestDataHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class SpaceshipControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private SpaceshipServiceImpl spaceshipServiceImpl;

    private SpaceshipDto spaceshipDto;

    private TestDataHelper dataHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        spaceshipDto = SpaceshipDto.builder()
                .id(1L)
                .name("Millennium Falcon")
                .build();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER", "ADMIN"})
    void getAllSpaceships_shouldReturnPageOfSpaceships_whenSpaceshipsExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        PageResponseDto<SpaceshipDto> spaceshipPageResponse = new PageResponseDto<>(
                List.of(spaceshipDto), // Mocked content list
                0,                     // pageNumber
                5,                     // pageSize
                1L,                    // totalElements
                1                      // totalPages
        );

        when(spaceshipServiceImpl.getAllSpaceships(pageable)).thenReturn(spaceshipPageResponse);

        mockMvc.perform(get("/api/spaceships")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(SPACESHIPS_PATH).isArray())
                .andExpect(jsonPath(CONTENT_LIST_PATH).value("Millennium Falcon"))
                .andExpect(jsonPath(PAGE_NUMBER_PATH).value(0))
                .andExpect(jsonPath(PAGE_SIZE_PATH).value(5))
                .andExpect(jsonPath(TOTAL_ELEMENTS_PATH).value(1))
                .andExpect(jsonPath(TOTAL_PAGES_PATH).value(1));

        verify(spaceshipServiceImpl, times(1)).getAllSpaceships(pageable);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER", "ADMIN"})
    void getAllSpaceships_shouldReturnEmptyPage_whenNoSpaceshipsExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        PageResponseDto<SpaceshipDto> emptyPageResponse = new PageResponseDto<>(
                Collections.emptyList(), // Empty content list
                0,
                5,
                0L,
                0
        );

        when(spaceshipServiceImpl.getAllSpaceships(pageable)).thenReturn(emptyPageResponse);

        mockMvc.perform(get("/api/spaceships")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(SPACESHIPS_PATH).isEmpty())
                .andExpect(jsonPath(PAGE_NUMBER_PATH).value(0))
                .andExpect(jsonPath(PAGE_SIZE_PATH).value(5))
                .andExpect(jsonPath(TOTAL_ELEMENTS_PATH).value(0))
                .andExpect(jsonPath(TOTAL_PAGES_PATH).value(0));

        verify(spaceshipServiceImpl, times(1)).getAllSpaceships(pageable);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER", "ADMIN"})
    void getSpaceshipById_shouldReturnSpaceship_whenSpaceshipExists() throws Exception {
        when(spaceshipServiceImpl.getSpaceshipById(1L)).thenReturn(Optional.of(spaceshipDto));

        mockMvc.perform(get("/api/spaceships/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(SPACESHIP_NAME).value(spaceshipDto.getName()));

        verify(spaceshipServiceImpl, times(1)).getSpaceshipById(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    void getSpaceshipById_shouldReturnNotFound_whenSpaceshipDoesNotExist() throws Exception {
        when(spaceshipServiceImpl.getSpaceshipById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/spaceships/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(spaceshipServiceImpl, times(1)).getSpaceshipById(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER", "ADMIN"})
    void searchSpaceshipsByName_shouldReturnSpaceships_whenMatchingSpaceshipsExist() throws Exception {
        SpaceshipsDataDto response = SpaceshipsDataDto.builder()
                .spaceships(List.of(spaceshipDto))
                .build();

        when(spaceshipServiceImpl.searchSpaceshipsByName("Falcon")).thenReturn(response);

        mockMvc.perform(get("/api/spaceships/search")
                        .param("name", "Falcon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(SPACESHIPS_ARRAY_PATH).isArray())
                .andExpect(jsonPath(LIST_FROM_SPACESHIP_NAME_PATH).value(spaceshipDto.getName()));

        verify(spaceshipServiceImpl, times(1)).searchSpaceshipsByName("Falcon");
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER", "ADMIN"})
    void searchSpaceshipsByName_shouldReturnEmptyList_whenNoMatchingSpaceshipsExist() throws Exception {
        SpaceshipsDataDto emptyResponse = SpaceshipsDataDto.builder()
                .spaceships(Collections.emptyList())
                .build();

        when(spaceshipServiceImpl.searchSpaceshipsByName("Nonexistent")).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/spaceships/search")
                        .param("name", "Nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(SPACESHIPS_ARRAY_PATH).isEmpty());

        verify(spaceshipServiceImpl, times(1)).searchSpaceshipsByName("Nonexistent");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createSpaceship_shouldCreateSpaceship_whenInputIsValid() throws Exception {
        when(spaceshipServiceImpl.createSpaceship(any(CreateSpaceshipRequestDto.class))).thenReturn(spaceshipDto);

        mockMvc.perform(post("/api/spaceships")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATION_NAME_REQUEST_BODY_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(SPACESHIP_NAME).value(spaceshipDto.getName()));

        verify(spaceshipServiceImpl, times(1))
                .createSpaceship(any(CreateSpaceshipRequestDto.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER"})
    void createSpaceship_shouldReturnForbidden_whenUserLacksAdminRole() throws Exception {
        mockMvc.perform(post("/api/spaceships")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATION_NAME_REQUEST_BODY_JSON))
                .andExpect(status().isForbidden());

        verify(spaceshipServiceImpl, times(0)).createSpaceship(any());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateSpaceship_shouldUpdateSpaceship_whenSpaceshipExists() throws Exception {
        when(spaceshipServiceImpl.updateSpaceship(eq(1L), any(CreateSpaceshipRequestDto.class)))
                .thenReturn(spaceshipDto);

        mockMvc.perform(put("/api/spaceships/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_NAME_REQUEST_BODY_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(SPACESHIP_NAME).value(spaceshipDto.getName()));

        verify(spaceshipServiceImpl, times(1))
                .updateSpaceship(eq(1L), any(CreateSpaceshipRequestDto.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateSpaceship_shouldReturnNotFound_whenSpaceshipDoesNotExist() throws Exception {
        when(spaceshipServiceImpl.updateSpaceship(eq(1L), any(CreateSpaceshipRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Spaceship not found"));

        mockMvc.perform(put("/api/spaceships/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_NAME_REQUEST_BODY_JSON))
                .andExpect(status().isNotFound());

        verify(spaceshipServiceImpl, times(1))
                .updateSpaceship(eq(1L), any(CreateSpaceshipRequestDto.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteSpaceship_shouldDeleteSpaceship_whenSpaceshipExists() throws Exception {
        mockMvc.perform(delete("/api/spaceships/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(spaceshipServiceImpl, times(1)).deleteSpaceship(1L);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void deleteSpaceship_shouldReturnNotFound_whenSpaceshipDoesNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Spaceship not found")).when(spaceshipServiceImpl).deleteSpaceship(1L);

        mockMvc.perform(delete("/api/spaceships/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(spaceshipServiceImpl, times(1)).deleteSpaceship(1L);
    }

    @Test
    @WithMockUser(username = "user")
    void deleteSpaceship_shouldReturnForbidden_whenUserLacksAdminRole() throws Exception {
        mockMvc.perform(delete("/api/spaceships/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(spaceshipServiceImpl, times(0)).deleteSpaceship(1L);
    }


}
