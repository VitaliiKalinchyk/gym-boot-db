package epam.task.gymbootdb.integration;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class UserControllerIntegrationTest {

    private static final String USERNAME = "Joe.Doe";
    private static final String PASSWORD = "password";
    private static final boolean STATUS = true;
    private static final String NEW_PASSWORD = "newPassword";
    private static final String BEARER = "Bearer ";
    private static final String WRONG = "wrong";
    private static final String USERS_CHANGE_PASSWORD = "/users/change-password";
    private static final String USERS_STATUS = "/users/status";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        var user = getUser();
        authorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(user));
        user.setPassword(passwordEncoder.encode(PASSWORD));

        userRepository.save(user);
    }

    @Test
    void changeUserActiveStatus() throws Exception {
        mockMvc.perform(patch(USERS_STATUS)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk());

        assertThat(userRepository.findByUsername(USERNAME)).isPresent()
                .get().extracting(User::isActive).isEqualTo(!STATUS);
    }

    @Test
    void changeUserActiveStatusUserDeleted() throws Exception {
        User user = getUser();
        user.setUsername(WRONG);
        authorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(user));

        mockMvc.perform(patch(USERS_STATUS)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("User with username " + WRONG + " was not found");
                });
    }

    @Test
    void changeUserPassword() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(getChangeUserPasswordRequest());

        mockMvc.perform(put(USERS_CHANGE_PASSWORD)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        assertThat(userRepository.findByUsername(USERNAME)).isPresent()
                .get().extracting(User::getPassword)
                .matches(encodedPassword -> passwordEncoder.matches(NEW_PASSWORD, encodedPassword));
    }

    @Test
    void changeUserPasswordInvalidRequest() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(put(USERS_CHANGE_PASSWORD)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("UserCredentials is required").contains("New password is required");
                });
    }

    private User getUser() {
        return User.builder()
                .firstName(USERNAME)
                .lastName(USERNAME)
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(STATUS)
                .roles(new HashSet<>())
                .build();
    }

    private ChangePasswordRequest getChangeUserPasswordRequest() {
        return ChangePasswordRequest.builder()
                .userCredentials(new UserCredentials(USERNAME, PASSWORD))
                .newPassword(NEW_PASSWORD)
                .build();
    }
}
