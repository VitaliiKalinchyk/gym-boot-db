package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.TraineeException;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.exception.UsernameException;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserRepository userRepository;
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

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private TrainerDto trainerRequest;
    private Trainer trainerEntity;
    private TrainerDto trainerResponse;
    private User user;
    private UserCredentials userCredentials;
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().firstName("Joe").lastName("Doe").build();
        trainerRequest = new TrainerDto();
        trainerEntity = new Trainer();
        trainerEntity.setUser(user);
        trainerResponse = new TrainerDto();
        userCredentials = new UserCredentials("testUsername", "testPassword");
        changePasswordRequest = new ChangePasswordRequest(1L, "testPassword","newPassword");
    }

    @Test
    void testCreateProfile() {
        when(trainerMapper.toEntity(trainerRequest)).thenReturn(trainerEntity);
        when(passwordGenerator.generatePassword()).thenReturn("testPassword");
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn("testUsername");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserCredentials result = trainerService.createProfile(trainerRequest);

        assertNotNull(result, "User credentials should not be null");
        assertEquals("testUsername", result.getUsername(), "Username should match");
        assertEquals("testPassword", result.getPassword(), "Password should match");
        assertTrue(user.isActive(), "User should be active");
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
    }

    @Test
    void testCreateProfileUsernameExists() {
        when(trainerMapper.toEntity(trainerRequest)).thenReturn(trainerEntity);
        when(passwordGenerator.generatePassword()).thenReturn("testPassword");
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn("testUsername");
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        when(userRepository.findUsernamesByUsernameStartsWith("testUsername")).thenReturn(List.of("testUsername"));
        when(nameGenerator.generateUsername("testUsername", List.of("testUsername")))
                .thenReturn("testUsername1");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserCredentials result = trainerService.createProfile(trainerRequest);

        assertNotNull(result, "User credentials should not be null");
        assertEquals("testUsername1", result.getUsername(), "Username should match");
        assertEquals("testPassword", result.getPassword(), "Password should match");
        assertTrue(user.isActive(), "User should be active");
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
    }

    @Test
    void testMatchCredentials() {
        when(trainerRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(trainerEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(),
                trainerEntity.getUser().getPassword())).thenReturn(true);

        assertDoesNotThrow(() -> trainerService.matchCredentials(userCredentials));
    }

    @Test
    void testMatchCredentialsNoUser() {
        TrainerException e = assertThrows(TrainerException.class,
                () -> trainerService.matchCredentials(userCredentials));
        assertEquals("Trainer with username " + userCredentials.getUsername() + " was not found",
                e.getMessage());
    }

    @Test
    void testMatchCredentialsIncorrectPassword() {
        when(trainerRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(trainerEntity));

        PasswordException e = assertThrows(PasswordException.class,
                () -> trainerService.matchCredentials(userCredentials));
        assertEquals("Wrong password", e.getMessage());
    }

    @Test
    void testUpdateTrainer() {
        trainerRequest.setUser(UserDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("Jane.Smith")
                .active(false).build());

        when(trainerRepository.findById(trainerRequest.getId())).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toDto(any())).thenReturn(trainerResponse);
        when(traineeMapper.toDtoList(any())).thenReturn(List.of(new TraineeDto()));

        TrainerDto result = trainerService.update(trainerRequest);

        assertNotNull(result, "TrainerResponse should not be null");
        assertEquals(trainerResponse, result, "TrainerResponse should match the expected value");
        assertEquals(trainerRequest.getUser().getUsername(), trainerEntity.getUser().getUsername());
        assertEquals(trainerRequest.getUser().getFirstName(), trainerEntity.getUser().getFirstName());
        assertEquals(trainerRequest.getUser().getLastName(), trainerEntity.getUser().getLastName());
        assertEquals(trainerRequest.getUser().isActive(), trainerEntity.getUser().isActive());
        assertEquals(1, result.getTrainees().size());
    }

    @Test
    void testUpdateTrainerNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class, () -> trainerService.update(trainerRequest));

        assertEquals("Trainer with id 0 was not found", e.getMessage());
    }

    @Test
    void testUpdateTrainerUsernameAlreadyExists() {
        trainerRequest.setId(1L);
        trainerRequest.setUser(UserDto.builder().firstName("Joe").lastName("Doe").username("Joe.Doe").build());

        when(trainerRepository.findById(trainerRequest.getId())).thenReturn(Optional.of(trainerEntity));
        when(userRepository.existsByUsername(any())).thenReturn(true);

        UsernameException e = assertThrows(UsernameException.class, () -> trainerService.update(trainerRequest));
        assertEquals("Username Joe.Doe already in use", e.getMessage());
    }

    @Test
    void testChangePassword() {
        when(trainerRepository.findById(changePasswordRequest.getId())).thenReturn(Optional.of(trainerEntity));
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), trainerEntity.getUser().getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(changePasswordRequest.getNewPassword())).thenReturn("newEncodedPassword");

        trainerService.changePassword(changePasswordRequest);

        assertEquals("newEncodedPassword", trainerEntity.getUser().getPassword());
    }

    @Test
    void testChangePasswordNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class,
                () -> trainerService.changePassword(changePasswordRequest));

        assertEquals("Trainer with id 1 was not found", e.getMessage());
    }

    @Test
    void testChangePasswordIncorrect() {
        when(trainerRepository.findById(changePasswordRequest.getId())).thenReturn(Optional.of(trainerEntity));
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), trainerEntity.getUser().getPassword()))
                .thenReturn(false);

        PasswordException e = assertThrows(PasswordException.class,
                () -> trainerService.changePassword(changePasswordRequest));

        assertEquals("Wrong password", e.getMessage());
    }

    @Test
    void testSetActiveStatus() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainerEntity));

        trainerService.setActiveStatus(1L, true);

        assertTrue(trainerEntity.getUser().isActive(), "User should be active");
    }

    @Test
    void testSetActiveStatusNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class,
                () -> trainerService.setActiveStatus(1, true));

        assertEquals("Trainer with id 1 was not found", e.getMessage());
    }

    @Test
    void testGetById() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toDto(trainerEntity)).thenReturn(trainerResponse);
        when(traineeMapper.toDtoList(any())).thenReturn(List.of(new TraineeDto()));

        TrainerDto result = trainerService.getById(1L);

        assertNotNull(result, "TrainerResponse should not be null");
        assertEquals(trainerResponse, result, "TrainerResponse should match the expected value");
        assertEquals(1, result.getTrainees().size());
    }

    @Test
    void testGetByIdNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class, () -> trainerService.getById(1L));

        assertEquals("Trainer with id 1 was not found", e.getMessage());
    }

    @Test
    void testGetTrainersNotAssignedToTrainee() {
        when(traineeRepository.existsById(1L)).thenReturn(true);
        when(trainerMapper.toDtoList(anyList())).thenReturn(List.of(trainerResponse));

        List<TrainerDto> result = trainerService.getTrainersNotAssignedToTrainee(1L);

        assertNotNull(result, "Trainer list should not be null");
        assertEquals(1, result.size(), "Should return one trainer");
        assertEquals(trainerResponse, result.get(0));
    }

    @Test
    void testGetTrainersNotAssignedToTraineeNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> trainerService.getTrainersNotAssignedToTrainee(1L));

        assertEquals("Trainee with id 1 was not found", e.getMessage());
    }
}