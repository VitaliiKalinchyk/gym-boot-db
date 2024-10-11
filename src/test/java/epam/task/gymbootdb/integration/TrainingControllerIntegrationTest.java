package epam.task.gymbootdb.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.dto.mapper.TrainingMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.TrainingType;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
public class TrainingControllerIntegrationTest {

    public static final String TRAINING_NAME = "training name";
    private static final String TRAINEE_USERNAME = "Joe.Doe";
    private static final String TRAINER_USERNAME = "Jane.Doe";
    private static final String PASSWORD = "password";
    public static final String BEARER = "Bearer ";
    public static final String TRAININGS = "/trainings";
    public static final String TRAININGS_TRAINEE = "/trainings/trainee";
    public static final String TRAININGS_TRAINER = "/trainings/trainer";

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
    private TrainingRepository trainingRepository;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private TraineeMapper traineeMapper;
    @Autowired
    private TrainerMapper trainerMapper;
    @Autowired
    private TrainingMapper trainingMapper;

    private String traineeAuthorizationHeader;
    private String trainerAuthorizationHeader;

    private TraineeDto savedTrainee;
    private TrainerDto savedTrainer;

    @BeforeEach
    void setUp() {
        traineeAuthorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(getUser(TRAINEE_USERNAME)));
        trainerAuthorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(getUser(TRAINER_USERNAME)));

        savedTrainee = traineeMapper.toDto(traineeRepository.save(getTrainee()));
        savedTrainer = trainerMapper.toDto(trainerRepository.save(getTrainer()));

        trainingRepository.save(trainingMapper.toEntity(getTraining()));
    }

    @Test
    void createTraining() throws Exception {
        long initialCount = trainingRepository.count();
        String jsonRequest = objectMapper.writeValueAsString(getTraining());

        mockMvc.perform(post(TRAININGS)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());

        assertThat(trainingRepository.count()).isEqualTo(initialCount + 1);
        assertThat(trainingRepository.findAll().stream()
                .anyMatch(training -> training.getName().equals(TRAINING_NAME))).isTrue();
    }

    @Test
    void createTrainingWrongUsername() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(getTraining());

        mockMvc.perform(post(TRAININGS)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + TRAINER_USERNAME + " was not found");
                });
    }

    @Test
    void createTrainingWithoutTrainer() throws Exception {
        TrainingDto trainingWithoutTrainer = getTraining();
        trainingWithoutTrainer.getTrainer().setId(0);

        String jsonRequest = objectMapper.writeValueAsString(trainingWithoutTrainer);

        mockMvc.perform(post(TRAININGS)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainer with id 0 was not found");
                });
    }

    @Test
    void createTrainingWithoutTrainingType() throws Exception {
        TrainingDto trainingWithoutTrainingType = getTraining();
        trainingWithoutTrainingType.getTrainingType().setId(0);

        String jsonRequest = objectMapper.writeValueAsString(trainingWithoutTrainingType);

        mockMvc.perform(post(TRAININGS)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("TrainingType with id 0 does not exist");
                });
    }

    @Test
    void createTrainingWithInvalidRequest() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(getInvalidTraining());

        mockMvc.perform(post(TRAININGS)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Date must be in the future")
                            .contains("TrainingType is required")
                            .contains("Duration must be greater than 10")
                            .contains("Trainer is required")
                            .contains("Name must be between 3 and 45 characters and " +
                                    "contain only letters, digits, dots or white spaces");
                });
    }

    @Test
    void getTraineeTrainings() throws Exception {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(7);

        mockMvc.perform(get(TRAININGS_TRAINEE)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .param("from-date", fromDate.toString())
                        .param("to-date", toDate.toString())
                        .param("trainer-id", String.valueOf(savedTrainer.getId()))
                        .param("training-type-id", String.valueOf(savedTrainer.getTrainingType().getId())))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINING_NAME);
                });
    }

    @Test
    void getTraineeTrainingsWithoutParams() throws Exception {
        mockMvc.perform(get(TRAININGS_TRAINEE)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINING_NAME);
                });
    }

    @Test
    void getTraineeTrainingsWithUnmatchedDate() throws Exception {
        LocalDate toDate = LocalDate.now();

        mockMvc.perform(get(TRAININGS_TRAINEE)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader)
                        .param("to-date", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).doesNotContain(TRAINING_NAME);
                });
    }

    @Test
    void getTraineeTrainingsWithWrongUsername() throws Exception {
        mockMvc.perform(get(TRAININGS_TRAINEE)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + TRAINER_USERNAME + " was not found");
                });
    }

    @Test
    void getTrainerTrainings() throws Exception {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(7);

        mockMvc.perform(get(TRAININGS_TRAINER)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader)
                        .param("from-date", fromDate.toString())
                        .param("to-date", toDate.toString())
                        .param("trainee-id", String.valueOf(savedTrainee.getId())))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINING_NAME);
                });
    }

    @Test
    void getTrainerTrainingsWithoutParams() throws Exception {
        mockMvc.perform(get(TRAININGS_TRAINER)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINING_NAME);
                });
    }

    @Test
    void getTrainerTrainingsWithUnmatchedDate() throws Exception {
        LocalDate toDate = LocalDate.now();

        mockMvc.perform(get(TRAININGS_TRAINER)
                        .header(HttpHeaders.AUTHORIZATION, trainerAuthorizationHeader)
                        .param("to-date", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).doesNotContain(TRAINING_NAME);
                });
    }

    @Test
    void getTrainerTrainingsWithWrongUsername() throws Exception {
        mockMvc.perform(get(TRAININGS_TRAINER)
                        .header(HttpHeaders.AUTHORIZATION, traineeAuthorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainer with username " + TRAINEE_USERNAME + " was not found");
                });
    }

    private TrainingDto getInvalidTraining() {
        return TrainingDto.builder()
                .name("1")
                .date(LocalDate.now().minusDays(1))
                .duration(5)
                .build();
    }

    private TrainingDto getTraining() {
        return TrainingDto.builder()
                .name(TRAINING_NAME)
                .date(LocalDate.now().plusDays(1))
                .duration(60)
                .trainee(savedTrainee)
                .trainer(savedTrainer)
                .trainingType(savedTrainer.getTrainingType())
                .build();
    }

    private Trainee getTrainee() {
        return Trainee.builder()
                .user(getUser(TRAINEE_USERNAME))
                .build();
    }

    private Trainer getTrainer() {
        return Trainer.builder()
                .user(getUser(TRAINER_USERNAME))
                .trainingType(getTrainingType())
                .build();
    }

    private TrainingType getTrainingType() {
        return trainingTypeRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> trainingTypeRepository.save(TrainingType.builder().name("Default Type").build()));
    }

    private User getUser(String username) {
        return User.builder()
                .firstName(username)
                .lastName(username)
                .username(username)
                .password(PASSWORD)
                .isActive(true)
                .roles(new HashSet<>())
                .build();
    }
}
