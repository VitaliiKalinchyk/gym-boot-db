package epam.task.gymbootdb.integration;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
class JwtIntegrationTest {

    private static final String USERNAME = "Joe.Doe";
    private static final String PASSWORD = "password";
    public static final String BEARER = "Bearer ";
    public static final String MALFORMED_TOKEN = "malformedToken";
    public static final String AUTH_LOGOUT = "/auth/logout";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        userRepository.save(getUser());
    }

    @Test
    void noTokenRequest() throws Exception {
        mockMvc.perform(post(AUTH_LOGOUT))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("You are not authenticated. Please provide a valid JWT token.");
                });
    }

    @Test
    void malformedTokenRequest() throws Exception {
        mockMvc.perform(post(AUTH_LOGOUT)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + MALFORMED_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Token format is invalid");
                });
    }

    @Test
    void expiredTokenRequest() throws Exception {
        String expiredAuthHeader = BEARER +
                jwtService.generateToken(new GymUserDetails(getUser()),-1000);

        mockMvc.perform(post(AUTH_LOGOUT)
                        .header(HttpHeaders.AUTHORIZATION, expiredAuthHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("JWT expired");
                });
    }

    @Test
    void invalidTokenSignatureRequest() throws Exception {
        String token = jwtService.generateToken(new GymUserDetails(getUser()));
        String invalidSignatureAuthHeader = BEARER + token.substring(0, token.length() - 1) + "A";

        mockMvc.perform(post(AUTH_LOGOUT)
                        .header(HttpHeaders.AUTHORIZATION, invalidSignatureAuthHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Invalid token signature");
                });
    }

    private User getUser() {
        return User.builder()
                .firstName(USERNAME)
                .lastName(USERNAME)
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(true)
                .roles(new HashSet<>())
                .build();
    }
}
