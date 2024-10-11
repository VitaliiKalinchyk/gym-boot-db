package epam.task.gymbootdb.integration;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.entity.TrainingType;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.TrainingTypeRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
public class TrainingTypeControllerIntegrationTest {

    public static final String TRAINING_TYPES = "/training-types";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private UserRepository userRepository;

    private static final String USERNAME = "Joe.Doe";
    private static final String PASSWORD = "password";
    public static final String BEARER = "Bearer ";

    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        authorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(getUser()));

        userRepository.save(getUser());
    }

    @Test
    void getAllTrainingTypesTrainingTypes() throws Exception {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();

        var resultActions = mockMvc.perform(get(TRAINING_TYPES)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(trainingTypes.size()));

        for (int i = 0; i < trainingTypes.size(); i++) {
            resultActions.andExpect(jsonPath("$[" + i + "].id").value(trainingTypes.get(i).getId()))
                    .andExpect(jsonPath("$[" + i + "].name").value(trainingTypes.get(i).getName()));
        }

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
