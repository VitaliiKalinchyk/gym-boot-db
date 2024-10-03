package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.TraineeException;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    public static final String USERNAME = "Joe";
    public static final String LAST_NAME = "Smith";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String JANE_SMITH = "Jane.Smith";
    public static final String JANE = "Jane";
    public static final String PASSWORD = "password";

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NameGenerator nameGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private TraineeDto traineeRequest;
    private TraineeDto traineeResponse;
    private Trainee traineeEntity;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().firstName(USERNAME).lastName(LAST_NAME).build();
        traineeRequest = new TraineeDto();
        traineeResponse = new TraineeDto();
        traineeEntity = new Trainee();
        traineeEntity.setUser(user);
    }

    @Test
    void testCreateProfile() {
        prepareCreateProfileMocks(false);

        UserCredentials result = traineeService.createProfile(traineeRequest);

        assertUserCredentials(result, USERNAME);
        assertTrue(user.isActive(), "User should be active");
        assertEquals(ENCODED_PASSWORD, user.getPassword(), "Password should be encoded");
    }

    @Test
    void testCreateProfileUsernameExists() {
        prepareCreateProfileMocks(true);

        UserCredentials result = traineeService.createProfile(traineeRequest);

        assertUserCredentials(result, USERNAME + 1);
        assertTrue(user.isActive(), "User should be active");
        assertEquals(ENCODED_PASSWORD, user.getPassword(), "Password should be encoded");
    }

    @Test
    void testUpdateTrainee() {
        traineeRequest.setBirthday(LocalDate.now());
        traineeRequest.setAddress("address");
        traineeRequest.setUser(createUserDto());

        when(traineeRepository.findByUserUsername(JANE_SMITH)).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toDto(any())).thenReturn(traineeResponse);
        when(trainerMapper.toDtoList(any())).thenReturn(List.of(new TrainerDto()));

        TraineeDto result = traineeService.update(traineeRequest);

        assertNotNull(result, "TraineeResponse should not be null");
        assertEquals(traineeResponse, result, "TraineeResponse should match the expected value");
        assertEquals(1, result.getTrainers().size());
        assertTraineeUpdatedFields();
    }

    @Test
    void testUpdateTraineeNoSuchTrainee() {
        traineeRequest.setUser(new UserDto());
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.update(traineeRequest));

        assertEquals("Trainee with username null was not found", e.getReason());
    }

    @Test
    void testGetByUsername() {
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toDto(traineeEntity)).thenReturn(traineeResponse);
        when(trainerMapper.toDtoList(any())).thenReturn(List.of(new TrainerDto()));

        TraineeDto result = traineeService.getByUsername(USERNAME);

        assertNotNull(result, "TraineeResponse should not be null");
        assertEquals(traineeResponse, result, "TraineeResponse should match the expected value");
        assertEquals(1, traineeResponse.getTrainers().size());
    }

    @Test
    void testGetByUsernameNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.getByUsername(USERNAME));

        assertEquals("Trainee with username Joe was not found", e.getReason());
    }

    @Test
    void testDeleteByUsername() {
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(traineeEntity));

        assertDoesNotThrow(() -> traineeService.deleteByUsername(USERNAME));
        verify(traineeRepository).delete(traineeEntity);
    }

    @Test
    void testDeleteByUsernameNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.deleteByUsername(USERNAME));
        assertEquals("Trainee with username Joe was not found", e.getReason());
    }

    @Test
    void testGetTrainersNotAssignedToTrainee() {
        TrainerDto trainerDto = new TrainerDto();

        when(traineeRepository.existsByUserUsername(USERNAME)).thenReturn(true);
        when(trainerMapper.toDtoList(anyList())).thenReturn(List.of(trainerDto));

        List<TrainerDto> result = traineeService.getTrainersNotAssignedToTrainee(USERNAME);

        assertNotNull(result, "Trainer list should not be null");
        assertEquals(1, result.size(), "Should return one trainer");
        assertEquals(trainerDto, result.getFirst());
    }

    @Test
    void testGetTrainersNotAssignedToTraineeNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.getTrainersNotAssignedToTrainee(USERNAME));

        assertEquals("Trainee with username Joe was not found", e.getReason());
    }

    @Test
    void testUpdateTraineeTrainers() {
        traineeEntity.setTrainers(new ArrayList<>());
        Trainer trainer = new Trainer();

        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(traineeEntity));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        traineeService.updateTraineeTrainers(USERNAME, 1L);

        assertEquals(1, traineeEntity.getTrainers().size());
        assertTrue(traineeEntity.getTrainers().contains(trainer));
    }

    @Test
    void testUpdateTraineeTrainersTrainerAlreadyInList () {
        Trainer trainer = new Trainer();
        traineeEntity.setTrainers(List.of(trainer));

        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(traineeEntity));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        traineeService.updateTraineeTrainers(USERNAME, 1L);

        assertEquals(1, traineeEntity.getTrainers().size());
        verify(traineeRepository, never()).save(any(Trainee.class));
    }

    @Test
    void testUpdateTraineeTrainersNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.updateTraineeTrainers(USERNAME, 1L));
        assertEquals("Trainee with username Joe was not found", e.getReason());
    }

    @Test
    void testUpdateTraineeTrainersNoSuchTrainer() {
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(traineeEntity));

        TrainerException e = assertThrows(TrainerException.class,
                () -> traineeService.updateTraineeTrainers(USERNAME, 2L));
        assertEquals("Trainer with id 2 was not found", e.getReason());
    }

    private void prepareCreateProfileMocks(boolean usernameExists) {
        when(traineeMapper.toEntity(traineeRequest)).thenReturn(traineeEntity);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn(USERNAME);
        when(userRepository.existsByUsername(anyString())).thenReturn(usernameExists);
        if (usernameExists) {
            when(userRepository.findUsernamesByUsernameStartsWith(USERNAME)).thenReturn(List.of(USERNAME));
            when(nameGenerator.generateUsername(USERNAME, List.of(USERNAME))).thenReturn(USERNAME + "1");
        }
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
    }

    private void assertUserCredentials(UserCredentials result, String expectedUsername) {
        assertNotNull(result, "User credentials should not be null");
        assertEquals(expectedUsername, result.getUsername(), "Username should match");
        assertEquals(PASSWORD, result.getPassword(), "Password should match");
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFirstName(JANE);
        userDto.setLastName(LAST_NAME);
        userDto.setUsername(JANE_SMITH);
        return userDto;
    }

    private void assertTraineeUpdatedFields() {
        assertEquals(traineeRequest.getBirthday(), traineeEntity.getBirthday());
        assertEquals(traineeRequest.getAddress(), traineeEntity.getAddress());
        assertEquals(traineeRequest.getUser().getFirstName(), traineeEntity.getUser().getFirstName());
        assertEquals(traineeRequest.getUser().getLastName(), traineeEntity.getUser().getLastName());
        assertEquals(traineeRequest.getUser().isActive(), traineeEntity.getUser().isActive());
    }
}
