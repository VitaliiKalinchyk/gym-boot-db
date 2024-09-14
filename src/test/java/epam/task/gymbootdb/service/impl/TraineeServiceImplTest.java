package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.TraineeException;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NameGenerator nameGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private TraineeCreateOrUpdateRequest traineeCreateRequest;
    private Trainee traineeEntity;
    private TraineeResponse traineeResponse;
    private User user;
    private UserCredentials userCredentials;

    @BeforeEach
    void setUp() {
        traineeCreateRequest = new TraineeCreateOrUpdateRequest();
        traineeEntity = new Trainee();
        traineeResponse = new TraineeResponse();
        user = User.builder().firstName("John").lastName("Smith").build();
        userCredentials = new UserCredentials("testUsername", "testPassword");
        traineeEntity.setUser(user);
    }

    @Test
    void testCreateProfile() {
        when(traineeMapper.toEntity(traineeCreateRequest)).thenReturn(traineeEntity);
        when(passwordGenerator.generatePassword()).thenReturn("testPassword");
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn("testUsername");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserCredentials result = traineeService.createProfile(traineeCreateRequest);

        assertNotNull(result, "User credentials should not be null");
        assertEquals("testUsername", result.getUsername(), "Username should match");
        assertEquals("testPassword", result.getPassword(), "Password should match");
        assertTrue(user.isActive(), "User should be active");
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
    }

    @Test
    void testCreateProfileUsernameExists() {
        when(traineeMapper.toEntity(traineeCreateRequest)).thenReturn(traineeEntity);
        when(passwordGenerator.generatePassword()).thenReturn("testPassword");
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn("testUsername");
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        when(userRepository.findUsernamesByUsernameStartsWith("testUsername")).thenReturn(List.of("testUsername"));
        when(nameGenerator.generateUsername("testUsername", List.of("testUsername")))
                .thenReturn("testUsername1");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserCredentials result = traineeService.createProfile(traineeCreateRequest);

        assertNotNull(result, "User credentials should not be null");
        assertEquals("testUsername1", result.getUsername(), "Username should match");
        assertEquals("testPassword", result.getPassword(), "Password should match");
        assertTrue(user.isActive(), "User should be active");
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
    }

    @Test
    void testMatchCredentials() {
        when(traineeRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(traineeEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(),
                traineeEntity.getUser().getPassword())).thenReturn(true);

        boolean result = traineeService.matchCredentials(userCredentials);

        assertTrue(result, "Credentials should match");
    }

    @Test
    void testMatchCredentialsNoUser() {
        boolean result = traineeService.matchCredentials(userCredentials);

        assertFalse(result, "Credentials should not match with no user");
    }

    @Test
    void testMatchCredentialsIncorrectPassword() {
        when(traineeRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(traineeEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(), traineeEntity.getUser().getPassword()))
                .thenReturn(false);

        boolean result = traineeService.matchCredentials(userCredentials);

        assertFalse(result, "Credentials should not match with wrong password");
    }

    @Test
    void testUpdateTrainee() {
        traineeCreateRequest.setId(1L);
        traineeCreateRequest.setUser(new UserCreateOrUpdateRequest("John", "Smith"));

        when(traineeRepository.findById(traineeCreateRequest.getId())).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toDto(any())).thenReturn(traineeResponse);

        TraineeResponse result = traineeService.update(traineeCreateRequest);

        assertNotNull(result, "TraineeResponse should not be null");
        assertEquals(traineeResponse, result, "TraineeResponse should match the expected value");
    }

    @Test
    void testUpdateTraineeNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.update(traineeCreateRequest));
        assertEquals("Trainee with id 0 was not found", e.getMessage());
    }

    @Test
    void testChangePassword() {
        when(traineeRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(traineeEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(), traineeEntity.getUser().getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        traineeService.changePassword(userCredentials, "newPassword");

        assertEquals("newEncodedPassword", traineeEntity.getUser().getPassword());
    }

    @Test
    void testChangePasswordNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.changePassword(userCredentials, "testPassword"));

        assertEquals("Trainee with username testUsername was not found", e.getMessage());
    }

    @Test
    void testChangePasswordIncorrect() {
        when(traineeRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(traineeEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(), traineeEntity.getUser().getPassword()))
                .thenReturn(false);

        PasswordException e = assertThrows(PasswordException.class,
                () -> traineeService.changePassword(userCredentials, "newPassword"));

        assertEquals("Wrong password", e.getMessage());
    }

    @Test
    void testSetActiveStatus() {
        when(traineeRepository.findByUserUsername("testUsername")).thenReturn(Optional.of(traineeEntity));

        traineeService.setActiveStatus("testUsername", true);

        assertTrue(traineeEntity.getUser().isActive(), "User should be active");
    }

    @Test
    void testSetActiveStatusNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.setActiveStatus("testUsername", true));

        assertEquals("Trainee with username testUsername was not found", e.getMessage());
    }

    @Test
    void testGetById() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toDto(traineeEntity)).thenReturn(traineeResponse);

        TraineeResponse result = traineeService.getById(1L);

        assertNotNull(result, "TraineeResponse should not be null");
        assertEquals(traineeResponse, result, "TraineeResponse should match the expected value");
    }

    @Test
    void testGetByIdNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.getById(1L));

        assertEquals("Trainee with id 1 was not found", e.getMessage());
    }

    @Test
    void testGetByUsername() {
        TraineeWithTrainersResponse response = TraineeWithTrainersResponse.builder().id(1L).build();
        when(traineeRepository.findByUserUsername("testUsername")).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toDtoWithTrainers(traineeEntity)).thenReturn(response);

        TraineeWithTrainersResponse result = traineeService.getByUsername("testUsername");

        assertNotNull(result, "TraineeResponse should not be null");
        assertEquals(response, result, "TraineeResponse should match the expected value");
    }

    @Test
    void testGetByUsernameNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.getByUsername("testUsername"));

        assertEquals("Trainee with username testUsername was not found", e.getMessage());
    }

    @Test
    void testDeleteByUsername() {
        when(traineeRepository.findByUserUsername("testUsername")).thenReturn(Optional.of(traineeEntity));

        assertDoesNotThrow(() -> traineeService.deleteByUsername("testUsername"));
        verify(traineeRepository).delete(traineeEntity);
    }

    @Test
    void testDeleteByUsernameNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.deleteByUsername("testUsername"));
        assertEquals("Trainee with username testUsername was not found", e.getMessage());
    }

    @Test
    public void testUpdateTraineeTrainers() {
        traineeEntity.setTrainers(new HashSet<>());
        List<Long> ids = List.of(1L);
        Trainer trainer = Trainer.builder().id(1L).build();
        List<Trainer> trainers = List.of(trainer);

        when(traineeRepository.findByUserUsername("testUsername")).thenReturn(Optional.of(traineeEntity));
        when(trainerRepository.findAllById(ids)).thenReturn(trainers);

        traineeService.updateTraineeTrainers("testUsername", ids);

        assertEquals(1, traineeEntity.getTrainers().size());
        assertTrue(traineeEntity.getTrainers().contains(trainer));
    }

    @Test
    public void testUpdateTraineeTrainersNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.deleteByUsername("testUsername"));
        assertEquals("Trainee with username testUsername was not found", e.getMessage());
    }
}