package com.develop.management.controller;

import com.develop.management.dto.user.UserLoginRequestDto;
import com.develop.management.dto.user.UserLoginResponseDto;
import com.develop.management.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth endpoints", description = "Endpoint to auth and get JWT token")
@RestController
@Slf4j
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "User login",
            description = "Authenticate an existing user by email and password. Returns a JWT token if credentials are valid."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully logged in, returning JWT token.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials provided."),
            @ApiResponse(responseCode = "400", description = "Request body is not valid.")
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(
            @Parameter(description = "Login request containing email and password.", required = true)
            @RequestBody @Valid UserLoginRequestDto request) {
        UserLoginResponseDto response = authenticationService.authenticate(request);
        log.info("Successfully logged in user");
        return ResponseEntity.ok(response);
    }

}
