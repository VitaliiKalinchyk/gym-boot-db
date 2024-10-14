package epam.task.gymbootdb.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.TrainingType;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.TrainerRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
public class TrainerControllerIntegrationTest {

    private static final String TRAINER_USERNAME = "Jane.Doe";
    private static final String PASSWORD = "password";
    public static final String BEARER = "Bearer ";
    public static final String WRONG_USERNAME = "wrongUsername";
    public static final String TRAINERS_USERNAME = "/trainers/{username}";
    public static final String TRAINERS_PROFILE = "/trainers/profile";
    public static final String TRAINERS = "/trainers";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainerMapper trainerMapper;

    private String trainerAuthorizationHeader;
    private String wrongAuthorizationHeader;

    private TrainerDto savedTrainer;

    @BeforeEach
    void setUp() {
        trainerAuthorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(getTrainerProfileUser(TRAINER_USERNAME)));
        wrongAuthorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(getTrainerProfileUser(WRONG_USERNAME)));

        savedTrainer = trainerMapper.toDto(trainerRepository.save(getTrainerProfileTrainer()));
        userRepository.save(getTrainerProfileUser(WRONG_USERNAME));
    }

    @Test
    void getTrainerProfileTrainerFromDb() throws Exception {
        mockMvc.perform(get(TRAINERS_USERNAME, TRAINER_USERNAME)
                .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINER_USERNAME);
                });
    }

    @Test
    void getTrainerProfileTrainerFromDbNoSuchTrainer() throws Exception {
        mockMvc.perform(get(TRAINERS_USERNAME, WRONG_USERNAME)
                .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainer with username " + WRONG_USERNAME + " was not found");
                });
    }

    @Test
    void getTrainerProfileTrainerByProfileFromDb() throws Exception {
        mockMvc.perform(get(TRAINERS_PROFILE)
                .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINER_USERNAME);
                });
    }

    @Test
    void getTrainerProfileTrainerFromDbByProfileDeletedTrainer() throws Exception {
        mockMvc.perform(get(TRAINERS_PROFILE)
                .header(HttpHeaders.AUTHORIZATION, wrongAuthorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainer with username " + WRONG_USERNAME + " was not found");
                });
    }

    @Test
    void createTrainerTrainer() throws Exception {
        mockMvc.perform(post(TRAINERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainer)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINER_USERNAME + "1");
                });
    }

    @Test
    void createTrainerTrainerInvalidRequest() throws Exception {
        savedTrainer.setTrainingType(null);
        savedTrainer.getUser().setFirstName("1234");
        savedTrainer.getUser().setLastName("1234");

        mockMvc.perform(post(TRAINERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainer)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("First name must be between 1 and 45 characters and contain only letters")
                            .contains("Last name must be between 1 and 45 characters and contain only letters")
                            .contains("TrainingType cannot be null");
                });
    }

    @Test
    void updateTrainerProfileTrainer() throws Exception {
        savedTrainer.getUser().setFirstName("Jim");
        savedTrainer.getUser().setLastName("Morris");

        mockMvc.perform(put(TRAINERS)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainer)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Jim")
                            .contains("Morris");
                });
    }

    @Test
    void updateTrainerProfileTrainerInvalidRequest() throws Exception {
        savedTrainer.getUser().setFirstName("1234");
        savedTrainer.getUser().setLastName("1234");

        mockMvc.perform(put(TRAINERS)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainer)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("First name must be between 1 and 45 characters and contain only letters")
                            .contains("Last name must be between 1 and 45 characters and contain only letters");
                });
    }

    @Test
    void updateTrainerProfileTrainerDeletedTrainer() throws Exception {
        mockMvc.perform(put(TRAINERS)
                        .header(HttpHeaders.AUTHORIZATION, wrongAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainer)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainer with username " + WRONG_USERNAME + " was not found");
                });
    }

    private Trainer getTrainerProfileTrainer() {
        return Trainer.builder()
                .user(getTrainerProfileUser(TRAINER_USERNAME))
                .trainingType(getTrainerProfileTrainingType())
                .build();
    }

    private TrainingType getTrainerProfileTrainingType() {
        return trainingTypeRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder().name("Default Type").build()));
    }

    private User getTrainerProfileUser(String username) {
        return User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .username(username)
                .password(PASSWORD)
                .isActive(true)
                .roles(new HashSet<>())
                .build();
    }
}
