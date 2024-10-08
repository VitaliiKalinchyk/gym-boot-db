package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Role;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.exception.TrainingTypeException;
import epam.task.gymbootdb.repository.RoleRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingTypeRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    public static final String USERNAME = "Joe";
    public static final String LAST_NAME = "Smith";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String JANE_SMITH = "Jane.Smith";
    public static final String JANE = "Jane";
    public static final String PASSWORD = "password";

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NameGenerator nameGenerator;
    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private TrainerDto trainerRequest;
    private Trainer trainerEntity;
    private TrainerDto trainerResponse;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().firstName(USERNAME).lastName(LAST_NAME).build();
        TrainingTypeDto trainingType = TrainingTypeDto.builder().id(1).build();
        trainerRequest = new TrainerDto();
        trainerRequest.setTrainingType(trainingType);
        trainerEntity = new Trainer();
        trainerEntity.setUser(user);
        trainerResponse = new TrainerDto();
    }

    @Test
    void testCreateProfile() {
        prepareCreateProfileMocks(false);

        UserCredentials result = trainerService.createProfile(trainerRequest);

        assertUserCredentials(result, USERNAME);
        assertTrue(user.isActive(), "User should be active");
        assertEquals(ENCODED_PASSWORD, user.getPassword(), "Password should be encoded");
        verify(loggingService).logDebugService(anyString(), anyString());
    }

    @Test
    void testCreateProfileNoSuchTrainingType() {
        TrainingTypeException e = assertThrows(TrainingTypeException.class,
                () -> trainerService.createProfile(trainerRequest));
        assertEquals("TrainingType with id 1 does not exist", e.getReason());
    }

    @Test
    void testCreateProfileUsernameExists() {
        prepareCreateProfileMocks(true);

        UserCredentials result = trainerService.createProfile(trainerRequest);

        assertUserCredentials(result, USERNAME + 1);
        assertTrue(user.isActive(), "User should be active");
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
    }

    @Test
    void testUpdateTrainer() {
        trainerRequest.setUser(createUserDto());

        when(trainerRepository.findByUserUsername(JANE_SMITH)).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toDto(any())).thenReturn(trainerResponse);
        when(traineeMapper.toDtoList(any())).thenReturn(List.of(new TraineeDto()));

        TrainerDto result = trainerService.update(trainerRequest);

        assertNotNull(result, "TrainerResponse should not be null");
        assertEquals(trainerResponse, result, "TrainerResponse should match the expected value");
        assertEquals(1, result.getTrainees().size());
        assertTrainerUpdatedFields();
        verify(loggingService).logDebugService(anyString(), anyString());
    }

    @Test
    void testUpdateTrainerNoSuchTrainer() {
        trainerRequest.setUser(new UserDto());
        TrainerException e = assertThrows(TrainerException.class, () -> trainerService.update(trainerRequest));

        assertEquals("Trainer with username null was not found", e.getReason());
    }

    @Test
    void testGetByUsername() {
        when(trainerRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toDto(trainerEntity)).thenReturn(trainerResponse);
        when(traineeMapper.toDtoList(any())).thenReturn(List.of(new TraineeDto()));

        TrainerDto result = trainerService.getByUsername(USERNAME);

        assertNotNull(result, "TrainerResponse should not be null");
        assertEquals(trainerResponse, result, "TrainerResponse should match the expected value");
        assertEquals(1, result.getTrainees().size());
        verify(loggingService).logDebugService(anyString(), anyString());
    }

    @Test
    void testGetByUsernameNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class, () -> trainerService.getByUsername(USERNAME));

        assertEquals("Trainer with username Joe was not found", e.getReason());
    }

    private void prepareCreateProfileMocks(boolean usernameExists) {
        when(trainingTypeRepository.existsById(anyLong())).thenReturn(true);
        when(trainerMapper.toEntity(trainerRequest)).thenReturn(trainerEntity);
        when(passwordGenerator.generatePassword()).thenReturn(PASSWORD);
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn(USERNAME);
        when(userRepository.existsByUsername(anyString())).thenReturn(usernameExists);
        if (usernameExists) {
            when(userRepository.findUsernamesByUsernameStartsWith(USERNAME)).thenReturn(List.of(USERNAME));
            when(nameGenerator.generateUsername(USERNAME, List.of(USERNAME))).thenReturn(USERNAME + "1");
        }
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(roleRepository.findByName(anyString())).thenReturn(new Role());
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

    private void assertTrainerUpdatedFields() {
        assertEquals(trainerRequest.getUser().getFirstName(), trainerEntity.getUser().getFirstName());
        assertEquals(trainerRequest.getUser().getLastName(), trainerEntity.getUser().getLastName());
        assertEquals(trainerRequest.getUser().isActive(), trainerEntity.getUser().isActive());
    }
}
