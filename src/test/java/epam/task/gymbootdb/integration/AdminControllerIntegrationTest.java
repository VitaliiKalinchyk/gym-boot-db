package epam.task.gymbootdb.integration;

import com.fasterxml.jackson.databind.ObjectMapper;

import epam.task.gymbootdb.dto.GymUserDetails;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.dto.mapper.TrainingMapper;
import epam.task.gymbootdb.entity.*;
import epam.task.gymbootdb.repository.*;
import epam.task.gymbootdb.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@Rollback
public class AdminControllerIntegrationTest {

    public static final String WRONG = "wrong";
    private static final String TRAINEE_USERNAME = "Joe.Doe";
    public static final String TRAINER_USERNAME = "Jane.Doe";
    public static final String TRAINING_NAME = "First Training";
    private static final String PASSWORD = "password";
    public static final String BEARER = "Bearer ";
    public static final String ADMIN_USERS_STATUS_USERNAME = "/admin/users/status/{username}";
    public static final String ADMIN_TRAINEES_USERNAME = "/admin/trainees/{username}";
    public static final String ADMIN_TRAINEES_UNASSIGNED_TRAINERS_USERNAME = "/admin/trainees/unassigned-trainers/{username}";
    public static final String ADMIN_TRAINEES_TRAINERS_USERNAME = "/admin/trainees/trainers/{username}";
    public static final String ADMIN_TRAINERS_USERNAME = "/admin/trainers/{username}";
    public static final String ADMIN_TRAININGS_USERNAME = "/admin/trainings/{username}";
    public static final String ADMIN_TRAININGS_TRAINEE_USERNAME = "/admin/trainings/trainee/{username}";
    public static final String ADMIN_TRAININGS_TRAINER_USERNAME = "/admin/trainings/trainer/{username}";

    @Value("${security.admin.name}")
    private String adminName;

    @Value("${security.admin.password}")
    private String adminPassword;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
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

    private String authorizationHeader;

    private TraineeDto savedTrainee;
    private TrainerDto savedTrainer;

    @BeforeEach
    void setUp() {
        authorizationHeader = BEARER + jwtService.generateToken(new GymUserDetails(getAdmin()));

        savedTrainee = traineeMapper.toDto(traineeRepository.save(getTrainee()));
        savedTrainer = trainerMapper.toDto(trainerRepository.save(getTrainer()));
        trainingRepository.save(trainingMapper.toEntity(getTraining()));
    }


    @Test
    void testChangeActiveStatus() throws Exception {
        boolean active = savedTrainee.getUser().isActive();

        mockMvc.perform(patch(ADMIN_USERS_STATUS_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk());

        assertThat(userRepository.findByUsername(TRAINEE_USERNAME)).isPresent()
                .get().extracting(User::isActive).isEqualTo(!active);
    }

    @Test
    void testChangeActiveStatusUserDeleted() throws Exception {
        mockMvc.perform(patch(ADMIN_USERS_STATUS_USERNAME, WRONG)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("User with username " + WRONG + " was not found");
                });
    }

    @Test
    void updateTrainee() throws Exception {
        savedTrainee.getUser().setFirstName("Jim");
        savedTrainee.getUser().setLastName("Morris");

        mockMvc.perform(put(ADMIN_TRAINEES_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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
    void updateTraineeInvalidRequest() throws Exception {
        savedTrainee.getUser().setFirstName("1234");
        savedTrainee.getUser().setLastName("1234");

        mockMvc.perform(put(ADMIN_TRAINEES_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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
    void updateTraineeDeletedTrainee() throws Exception {
        mockMvc.perform(put(ADMIN_TRAINEES_USERNAME, WRONG)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainee)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + WRONG + " was not found");
                });
    }

    @Test
    void deleteTrainee() throws Exception {
        mockMvc.perform(delete(ADMIN_TRAINEES_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk());

        assertThat(traineeRepository.existsByUserUsername(TRAINEE_USERNAME)).isFalse();
    }

    @Test
    void deleteTraineeWrongUsername() throws Exception {
        mockMvc.perform(delete(ADMIN_TRAINEES_USERNAME, WRONG)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + WRONG + " was not found");
                });
    }

    @Test
    void getTrainersNotAssignedToTrainee() throws Exception {
        mockMvc.perform(get(ADMIN_TRAINEES_UNASSIGNED_TRAINERS_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINER_USERNAME);
                });
    }

    @Test
    void updateTraineeTrainers() throws Exception {
        mockMvc.perform(put(ADMIN_TRAINEES_TRAINERS_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .param("trainerId", String.valueOf(savedTrainer.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(get(ADMIN_TRAINEES_UNASSIGNED_TRAINERS_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).doesNotContain(TRAINER_USERNAME);
                });
    }


    @Test
    void updateTrainer() throws Exception {
        savedTrainer.getUser().setFirstName("Jim");
        savedTrainer.getUser().setLastName("Morris");

        mockMvc.perform(put(ADMIN_TRAINERS_USERNAME, TRAINER_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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
    void updateTrainerInvalidRequest() throws Exception {
        savedTrainer.getUser().setFirstName("1234");
        savedTrainer.getUser().setLastName("1234");

        mockMvc.perform(put(ADMIN_TRAINERS_USERNAME, TRAINER_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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
    void updateTrainerDeletedTrainer() throws Exception {
        mockMvc.perform(put(ADMIN_TRAINERS_USERNAME, WRONG)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedTrainer)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainer with username " + WRONG + " was not found");
                });
    }


    @Test
    void createTraining() throws Exception {
        long initialCount = trainingRepository.count();
        String jsonRequest = objectMapper.writeValueAsString(getTraining());

        mockMvc.perform(post(ADMIN_TRAININGS_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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

        mockMvc.perform(post(ADMIN_TRAININGS_USERNAME, WRONG)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + WRONG + " was not found");
                });
    }

    @Test
    void createTrainingWithoutTrainer() throws Exception {
        TrainingDto trainingWithoutTrainer = getTraining();
        trainingWithoutTrainer.getTrainer().setId(0);

        String jsonRequest = objectMapper.writeValueAsString(trainingWithoutTrainer);

        mockMvc.perform(post(ADMIN_TRAININGS_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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

        mockMvc.perform(post(ADMIN_TRAININGS_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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

        mockMvc.perform(post(ADMIN_TRAININGS_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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

        mockMvc.perform(get(ADMIN_TRAININGS_TRAINEE_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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
        mockMvc.perform(get(ADMIN_TRAININGS_TRAINEE_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINING_NAME);
                });
    }

    @Test
    void getTraineeTrainingsWithUnmatchedDate() throws Exception {
        LocalDate toDate = LocalDate.now();

        mockMvc.perform(get(ADMIN_TRAININGS_TRAINEE_USERNAME, TRAINEE_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .param("to-date", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).doesNotContain(TRAINING_NAME);
                });
    }

    @Test
    void getTraineeTrainingsWithWrongUsername() throws Exception {
        mockMvc.perform(get(ADMIN_TRAININGS_TRAINEE_USERNAME, WRONG)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainee with username " + WRONG + " was not found");
                });
    }

    @Test
    void getTrainerTrainings() throws Exception {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now().plusDays(7);

        mockMvc.perform(get(ADMIN_TRAININGS_TRAINER_USERNAME, TRAINER_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
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
        mockMvc.perform(get(ADMIN_TRAININGS_TRAINER_USERNAME, TRAINER_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains(TRAINING_NAME);
                });
    }

    @Test
    void getTrainerTrainingsWithUnmatchedDate() throws Exception {
        LocalDate toDate = LocalDate.now();

        mockMvc.perform(get(ADMIN_TRAININGS_TRAINER_USERNAME, TRAINER_USERNAME)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .param("to-date", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).doesNotContain(TRAINING_NAME);
                });
    }

    @Test
    void getTrainerTrainingsWithWrongUsername() throws Exception {
        mockMvc.perform(get(ADMIN_TRAININGS_TRAINER_USERNAME, WRONG)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assertThat(content).contains("Trainer with username " + WRONG + " was not found");
                });
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
                .firstName("Jane")
                .lastName("Doe")
                .username(username)
                .password(PASSWORD)
                .isActive(true)
                .roles(new HashSet<>())
                .build();
    }
    private User getAdmin() {
        return User.builder()
                .username(adminName)
                .password(adminPassword)
                .roles(new HashSet<>(Set.of(Role.builder().name("ROLE_ADMIN").build())))
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

    private TrainingDto getInvalidTraining() {
        return TrainingDto.builder()
                .name("1")
                .date(LocalDate.now().minusDays(1))
                .duration(5)
                .build();
    }
}
