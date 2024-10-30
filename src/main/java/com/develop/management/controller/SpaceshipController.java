package com.develop.management.controller;

import com.develop.management.dto.spaceship.CreateSpaceshipRequestDto;
import com.develop.management.dto.spaceship.SpaceshipDto;
import com.develop.management.exception.ErrorResponse;
import com.develop.management.dto.spaceship.PageResponseDto;
import com.develop.management.dto.spaceship.SpaceshipsDataDto;
import com.develop.management.service.SpaceshipServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/spaceships")
@Tag(name = "Spaceship API", description = "CRUD operations for spaceships from movies and series")
@ApiResponses({@ApiResponse(responseCode = "400", description = "Invalid input provided", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))), @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))), @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
public class SpaceshipController {

    private final SpaceshipServiceImpl spaceshipServiceImpl;

    public SpaceshipController(SpaceshipServiceImpl spaceshipServiceImpl) {
        this.spaceshipServiceImpl = spaceshipServiceImpl;
    }

    @Operation(summary = "Get all spaceships", description = "Retrieve all spaceships with pagination support", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all spaceships")
    @GetMapping
    public PageResponseDto<SpaceshipDto> getAllSpaceships(Pageable pageable) {
        PageResponseDto<SpaceshipDto> allSpaceships = spaceshipServiceImpl.getAllSpaceships(pageable);
        log.info("Retrieved spaceships: {}", allSpaceships.getContent());
        return allSpaceships;
    }

    @Operation(summary = "Get spaceship by ID", description = "Retrieve a spaceship by its ID", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successfully retrieved the spaceship", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpaceshipDto.class))), @ApiResponse(responseCode = "404", description = "Spaceship not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{id}")
    public ResponseEntity<SpaceshipDto> getSpaceshipById(@Parameter(description = "ID of the spaceship to be retrieved") @PathVariable Long id) {
        ResponseEntity<SpaceshipDto> spaceshipDtoResponse = spaceshipServiceImpl
                .getSpaceshipById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        log.info("Retrieved spaceship: {}", spaceshipDtoResponse.getBody());
        return spaceshipDtoResponse;
    }

    @Operation(summary = "Search spaceships by name", description = "Search for spaceships containing the specified name", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the spaceships matching the query")
    @GetMapping("/search")
    public SpaceshipsDataDto searchSpaceshipsByName(@Parameter(description = "Part of the name to search for") @RequestParam String name) {
        SpaceshipsDataDto spaceshipsDataDto = spaceshipServiceImpl.searchSpaceshipsByName(name);
        log.info("Retrieved spaceships by it's name: {}", spaceshipsDataDto.getSpaceships());
        return spaceshipsDataDto;
    }

    @Operation(summary = "Create a new spaceship", description = "Create a new spaceship entity", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Spaceship created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpaceshipDto.class))), @ApiResponse(responseCode = "409", description = "Conflict - Spaceship with similar attributes already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SpaceshipDto> createSpaceship(@Parameter(description = "Spaceship entity to be created") @RequestBody CreateSpaceshipRequestDto spaceship) {
        SpaceshipDto createdSpaceship = spaceshipServiceImpl.createSpaceship(spaceship);
        log.info("Successfully created spaceship: {}", createdSpaceship);
        return new ResponseEntity<>(createdSpaceship, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a spaceship", description = "Update an existing spaceship by ID", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Spaceship updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpaceshipDto.class))), @ApiResponse(responseCode = "404", description = "Spaceship not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SpaceshipDto> updateSpaceship(@Parameter(description = "ID of the spaceship to be updated") @PathVariable Long id, @Parameter(description = "Updated spaceship entity") @RequestBody CreateSpaceshipRequestDto spaceship) {
        ResponseEntity<SpaceshipDto> updateResponse = ResponseEntity.ok(spaceshipServiceImpl.updateSpaceship(id, spaceship));
        log.info("Successfully updated spaceship: {}", updateResponse.getBody());
        return updateResponse;
    }

    @Operation(summary = "Delete a spaceship", description = "Delete a spaceship by its ID", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Spaceship deleted successfully"), @ApiResponse(responseCode = "404", description = "Spaceship not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteSpaceship(@Parameter(description = "ID of the spaceship to be deleted") @PathVariable Long id) {
        spaceshipServiceImpl.deleteSpaceship(id);
        log.info("Successfully deleted spaceship with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

}
