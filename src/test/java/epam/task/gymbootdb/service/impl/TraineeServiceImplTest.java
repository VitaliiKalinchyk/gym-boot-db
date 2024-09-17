package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
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

import java.util.ArrayList;
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
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    void setUp() {
        traineeCreateRequest = new TraineeCreateOrUpdateRequest();
        traineeEntity = new Trainee();
        traineeResponse = new TraineeResponse();
        user = User.builder().firstName("John").lastName("Smith").build();
        userCredentials = new UserCredentials("testUsername", "testPassword");
        changePasswordRequest = new ChangePasswordRequest(userCredentials, "newPassword");
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

        assertDoesNotThrow(() -> traineeService.matchCredentials(userCredentials));
    }

    @Test
    void testMatchCredentialsNoUser() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.matchCredentials(userCredentials));
        assertEquals("Trainee with username " + userCredentials.getUsername() + " was not found",
                e.getMessage());
    }

    @Test
    void testMatchCredentialsIncorrectPassword() {
        when(traineeRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(traineeEntity));

        PasswordException e = assertThrows(PasswordException.class,
                () -> traineeService.matchCredentials(userCredentials));
        assertEquals("Wrong password", e.getMessage());
    }

    @Test
    void testUpdateTrainee() {
        traineeCreateRequest.setId(1L);
        traineeCreateRequest.setUser(UserDto.builder().firstName("Joe").lastName("Doe").build());

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
        when(passwordEncoder.encode(changePasswordRequest.getNewPassword())).thenReturn("newEncodedPassword");

        traineeService.changePassword(changePasswordRequest);

        assertEquals("newEncodedPassword", traineeEntity.getUser().getPassword());
    }

    @Test
    void testChangePasswordNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.changePassword(changePasswordRequest));

        assertEquals("Trainee with username testUsername was not found", e.getMessage());
    }

    @Test
    void testChangePasswordIncorrect() {
        when(traineeRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(traineeEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(), traineeEntity.getUser().getPassword()))
                .thenReturn(false);

        PasswordException e = assertThrows(PasswordException.class,
                () -> traineeService.changePassword(changePasswordRequest));

        assertEquals("Wrong password", e.getMessage());
    }

    @Test
    void testSetActiveStatus() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));

        traineeService.setActiveStatus(1L, true);

        assertTrue(traineeEntity.getUser().isActive(), "User should be active");
    }

    @Test
    void testSetActiveStatusNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.setActiveStatus(1L, true));

        assertEquals("Trainee with id 1 was not found", e.getMessage());
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
    void testDeleteById() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));

        assertDoesNotThrow(() -> traineeService.deleteById(1L));
        verify(traineeRepository).delete(traineeEntity);
    }

    @Test
    void testDeleteByIdNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.deleteById(1L));
        assertEquals("Trainee with id 1 was not found", e.getMessage());
    }

    @Test
    public void testUpdateTraineeTrainers() {
        traineeEntity.setTrainers(new ArrayList<>());
        Trainer trainer = new Trainer();

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));

        traineeService.updateTraineeTrainers(1L, 2L);

        assertEquals(1, traineeEntity.getTrainers().size());
        assertTrue(traineeEntity.getTrainers().contains(trainer));
    }

    @Test
    public void testUpdateTraineeTrainersNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.updateTraineeTrainers(1L, 2L));
        assertEquals("Trainee with id 1 was not found", e.getMessage());
    }

    @Test
    public void testUpdateTraineeTrainersNoSuchTrainer() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));

        TrainerException e = assertThrows(TrainerException.class,
                () -> traineeService.updateTraineeTrainers(1L, 2L));
        assertEquals("Trainer with id 2 was not found", e.getMessage());
    }
}