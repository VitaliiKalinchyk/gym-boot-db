package epam.task.gymbootdb.integration;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.JwtBlacklistRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.JwtService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
class AuthControllerIntegrationTest {

    private static final String USERNAME = "Joe.Doe";
    private static final String PASSWORD = "password";
    public static final String WRONG_VALUE = "wrong";
    public static final String BEARER = "Bearer ";
    public static final String AUTH_LOGIN = "/auth/login";
    public static final String AUTH_LOGOUT = "/auth/logout";
    public static final String TOO_MUCH_ATTEMPTS = "tooMuchAttempts";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepository;
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
    void login() throws Exception {
        var request = new UserCredentials(USERNAME, PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    void loginWithTooManyAttempts() throws Exception {
        var request = new UserCredentials(TOO_MUCH_ATTEMPTS, PASSWORD);
        String jsonRequest = objectMapper.writeValueAsString(request);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post(AUTH_LOGIN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest));
        }

        mockMvc.perform(post(AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains(TOO_MUCH_ATTEMPTS + " is locked. Please try again later.");
                });
    }

    @Test
    void loginWithInvalidRequest() throws Exception {
        var request = new UserCredentials();
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("Password is required").contains("Username is required");
                });
    }

    @Test
    void loginWithWrongUsername() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(new UserCredentials(WRONG_VALUE, PASSWORD));

        mockMvc.perform(post(AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Wrong username or password");
                });
    }

    @Test
    void loginWithWrongPassword() throws Exception {
        var request = new UserCredentials(USERNAME, WRONG_VALUE);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Wrong username or password");
                });
    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(post(AUTH_LOGOUT)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk());

        assertThat(jwtBlacklistRepository.existsById(authorizationHeader.substring(BEARER.length()))).isTrue();
    }

    @Test
    void performRequestAfterLogout() throws Exception {
        mockMvc.perform(post(AUTH_LOGOUT)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader));

        mockMvc.perform(post(AUTH_LOGOUT)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("You are not authenticated. Please provide a valid JWT token.");
                    System.err.println(response);
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
