package epam.task.gymbootdb.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.TrainingType;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingTypeRepository;
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

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
class TraineeControllerIntegrationTest {

    private static final String TRAINEE_USERNAME = "Joe.Doe";
    private static final String TRAINER_USERNAME = "Jane.Doe";
    private static final String PASSWORD = "password";
    private static final String BEARER = "Bearer ";
    private static final String TRAINEES_USERNAME = "/trainees/{username}";
    private static final String TRAINEES_PROFILE = "/trainees/profile";
    private static final String TRAINEES = "/trainees";
    private static final String TRAINEES_UNASSIGNED_TRAINERS = "/trainees/unassigned-trainers";
    private static final String TRAINEES_TRAINERS = "/trainees/trainers";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private TraineeMapper traineeMapper;
    @Autowired
    private TrainerMapper trainerMapper;

    private String traineeAuthorizationHeader;
    private String trainerAuthorizationHeader;
    private TraineeDto savedTrainee;
    private TrainerDto savedTrainer;

    @BeforeEach
    void setUp() {
        traineeAuthorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(getTraineeProfileUser(TRAINEE_USERNAME)));
        trainerAuthorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(getTraineeProfileUser(TRAINER_USERNAME)));

        savedTrainee = traineeMapper.toDto(traineeRepository.save(getTraineeProfileTrainee()));
        savedTrainer = trainerMapper.toDto(trainerRepository.save(getTraineeProfileTrainer()));
    }

    @Test
    void getTraineeProfileTraineeFromDb() throws Exception {
        mockMvc.perform(get(TRAINEES_USERNAME, TRAINEE_USERNAME)
                .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINEE_USERNAME);
                });
    }

    @Test
    void getTraineeProfileTraineeFromDbNoSuchTrainee() throws Exception {
        mockMvc.perform(get(TRAINEES_USERNAME, TRAINER_USERNAME)
                .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + TRAINER_USERNAME + " was not found");
                });
    }

    @Test
    void getTraineeProfileTraineeByProfileFromDb() throws Exception {
        mockMvc.perform(get(TRAINEES_PROFILE)
                .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINEE_USERNAME);
                });
    }

    @Test
    void getTraineeProfileTraineeFromDbByProfileDeletedTrainee() throws Exception {
        mockMvc.perform(get(TRAINEES_PROFILE)
                .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + TRAINER_USERNAME + " was not found");
                });
    }

    @Test
    void createTraineeTrainee() throws Exception {
        mockMvc.perform(post(TRAINEES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainee)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINER_USERNAME + "1");
                });
    }

    @Test
    void createTraineeTraineeInvalidRequest() throws Exception {
        savedTrainee.getUser().setFirstName("1234");
        savedTrainee.getUser().setLastName("1234");
        savedTrainee.setBirthday(LocalDate.now().plusDays(1));
        savedTrainee.setAddress("A".repeat(121));

        mockMvc.perform(post(TRAINEES)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainee)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("First name must be between 1 and 45 characters and contain only letters")
                            .contains("Last name must be between 1 and 45 characters and contain only letters")
                            .contains("Birthday cannot be in the future")
                            .contains("Address cannot exceed 120 characters");
                });
    }

    @Test
    void updateTraineeProfileTrainee() throws Exception {
        savedTrainee.getUser().setFirstName("Jim");
        savedTrainee.getUser().setLastName("Morris");

        mockMvc.perform(put(TRAINEES)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainee)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Jim")
                            .contains("Morris");
                });
    }

    @Test
    void updateTraineeProfileTraineeInvalidRequest() throws Exception {
        savedTrainee.getUser().setFirstName("1234");
        savedTrainee.getUser().setLastName("1234");

        mockMvc.perform(put(TRAINEES)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainee)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("First name must be between 1 and 45 characters and contain only letters")
                            .contains("Last name must be between 1 and 45 characters and contain only letters");
                });
    }

    @Test
    void updateTraineeProfileTraineeDeletedTrainee() throws Exception {
        mockMvc.perform(put(TRAINEES)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainee)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + TRAINER_USERNAME + " was not found");
                });
    }

    @Test
    void deleteTraineeTrainee() throws Exception {
        mockMvc.perform(delete(TRAINEES)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader))
                .andExpect(status().isOk());

        assertThat(traineeRepository.existsByUserUsername(TRAINEE_USERNAME)).isFalse();
    }

    @Test
    void deleteTraineeTraineeWrongUsername() throws Exception {
        mockMvc.perform(delete(TRAINEES)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + TRAINER_USERNAME + " was not found");
                });
    }

    @Test
    void getTraineeProfileTrainersNotAssignedToTrainee() throws Exception {
        mockMvc.perform(get(TRAINEES_UNASSIGNED_TRAINERS)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINER_USERNAME);
                });
    }

    @Test
    void updateTraineeProfileTraineeTrainers() throws Exception {
        mockMvc.perform(put(TRAINEES_TRAINERS)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .param("trainerId", String.valueOf(savedTrainer.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(get(TRAINEES_UNASSIGNED_TRAINERS)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).doesNotContain(TRAINER_USERNAME);
                });
    }

    private Trainee getTraineeProfileTrainee() {
        return Trainee.builder()
                .user(getTraineeProfileUser(TRAINEE_USERNAME))
                .build();
    }

    private Trainer getTraineeProfileTrainer() {
        return Trainer.builder()
                .user(getTraineeProfileUser(TRAINER_USERNAME))
                .trainingType(getTraineeProfileTrainingType())
                .build();
    }

    private TrainingType getTraineeProfileTrainingType() {
        return trainingTypeRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder().name("Default Type").build()));
    }

    private User getTraineeProfileUser(String username) {
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
