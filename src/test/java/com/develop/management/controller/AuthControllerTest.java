package com.develop.management.controller;

import com.develop.management.dto.user.UserLoginRequestDto;
import com.develop.management.dto.user.UserLoginResponseDto;
import com.develop.management.exception.AuthenticationException;
import com.develop.management.security.AuthenticationService;
import com.develop.management.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.develop.management.controller.TestDataHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtUtils jwtUtils;

    private TestDataHelper dataHelper;

    @Test
    public void login_ShouldReturnJwtToken_WhenCredentialsAreValid() throws Exception {
        UserLoginResponseDto responseDto = new UserLoginResponseDto("jwt-token-example");
        when(authenticationService.authenticate(any(UserLoginRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDENTIALS_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(TOKEN_JSON_PATH).value("jwt-token-example"));
    }

    @Test
    public void login_ShouldReturn401_WhenCredentialsAreInvalid() throws Exception {
        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenThrow(new AuthenticationException(INVALID_CREDENTIALS_ERROR));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_CREDENTIALS_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath(MESSAGE_JSON_PATH).value(UNAUTHORIZED_MESSAGE))
                .andExpect(jsonPath(ERRORS_JSON_PATH).value(INVALID_CREDENTIALS_ERROR));
    }

    @Test
    public void login_ShouldReturn400_WhenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_REQUEST_BODY_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MESSAGE_JSON_PATH).value(REQUEST_BODY_INVALID_MESSAGE))
                .andExpect(jsonPath(ERRORS_JSON_PATH).isArray())
                .andExpect(jsonPath(ERRORS_JSON_PATH).isNotEmpty());
    }
}
