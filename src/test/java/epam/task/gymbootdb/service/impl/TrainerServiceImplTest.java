package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TrainerCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TrainerResponse;
import epam.task.gymbootdb.dto.UserCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
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
    private PasswordGenerator passwordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NameGenerator nameGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private TrainerCreateOrUpdateRequest trainerCreateRequest;
    private Trainer trainerEntity;
    private TrainerResponse trainerResponse;
    private User user;
    private UserCredentials userCredentials;

    @BeforeEach
    void setUp() {
        trainerCreateRequest = new TrainerCreateOrUpdateRequest();
        trainerEntity = new Trainer();
        trainerResponse = new TrainerResponse();
        user = User.builder().firstName("Joe").lastName("Doe").build();
        userCredentials = new UserCredentials("testUsername", "testPassword");
        trainerEntity.setUser(user);
    }

    @Test
    void testCreateProfile() {
        when(trainerMapper.toEntity(trainerCreateRequest)).thenReturn(trainerEntity);
        when(passwordGenerator.generatePassword()).thenReturn("testPassword");
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn("testUsername");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserCredentials result = trainerService.createProfile(trainerCreateRequest);

        assertNotNull(result, "User credentials should not be null");
        assertEquals("testUsername", result.getUsername(), "Username should match");
        assertEquals("testPassword", result.getPassword(), "Password should match");
        assertTrue(user.isActive(), "User should be active");
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
    }

    @Test
    void testCreateProfileUsernameExists() {
        when(trainerMapper.toEntity(trainerCreateRequest)).thenReturn(trainerEntity);
        when(passwordGenerator.generatePassword()).thenReturn("testPassword");
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn("testUsername");
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        when(userRepository.findUsernamesByUsernameStartsWith("testUsername")).thenReturn(List.of("testUsername"));
        when(nameGenerator.generateUsername("testUsername", List.of("testUsername")))
                .thenReturn("testUsername1");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserCredentials result = trainerService.createProfile(trainerCreateRequest);

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

        boolean result = trainerService.matchCredentials(userCredentials);

        assertTrue(result, "Credentials should match");
    }

    @Test
    void testMatchCredentialsNoUser() {
        boolean result = trainerService.matchCredentials(userCredentials);

        assertFalse(result, "Credentials should not match with no user");
    }

    @Test
    void testMatchCredentialsIncorrectPassword() {
        when(trainerRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(trainerEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(), trainerEntity.getUser().getPassword())).
                thenReturn(false);

        boolean result = trainerService.matchCredentials(userCredentials);

        assertFalse(result, "Credentials should not match with wrong password");
    }

    @Test
    void testUpdateTrainer() {
        trainerCreateRequest.setId(1L);
        trainerCreateRequest.setUser(new UserCreateOrUpdateRequest("Joe", "Doe"));

        when(trainerRepository.findById(trainerCreateRequest.getId())).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toDto(any())).thenReturn(trainerResponse);

        TrainerResponse result = trainerService.update(trainerCreateRequest);

        assertNotNull(result, "TrainerResponse should not be null");
        assertEquals(trainerResponse, result, "TrainerResponse should match the expected value");
    }

    @Test
    void testUpdateTrainerNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class, () -> trainerService.update(trainerCreateRequest));

        assertEquals("Trainer with id 0 was not found", e.getMessage());
    }

    @Test
    void testChangePassword() {
        when(trainerRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(trainerEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(), trainerEntity.getUser().getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        trainerService.changePassword(userCredentials, "newPassword");

        assertEquals("newEncodedPassword", trainerEntity.getUser().getPassword());
    }

    @Test
    void testChangePasswordNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class,
                () -> trainerService.changePassword(userCredentials, "testPassword"));

        assertEquals("Trainer with username testUsername was not found", e.getMessage());
    }

    @Test
    void testChangePasswordIncorrect() {
        when(trainerRepository.findByUserUsername(userCredentials.getUsername())).thenReturn(Optional.of(trainerEntity));
        when(passwordEncoder.matches(userCredentials.getPassword(), trainerEntity.getUser().getPassword()))
                .thenReturn(false);

        PasswordException e = assertThrows(PasswordException.class,
                () -> trainerService.changePassword(userCredentials, "newPassword"));

        assertEquals("Wrong password", e.getMessage());
    }

    @Test
    void testSetActiveStatus() {
        when(trainerRepository.findByUserUsername("testUsername")).thenReturn(Optional.of(trainerEntity));

        trainerService.setActiveStatus("testUsername", true);

        assertTrue(trainerEntity.getUser().isActive(), "User should be active");
    }

    @Test
    void testSetActiveStatusNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class,
                () -> trainerService.setActiveStatus("testUsername", true));

        assertEquals("Trainer with username testUsername was not found", e.getMessage());
    }

    @Test
    void testGetById() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toDto(trainerEntity)).thenReturn(trainerResponse);

        TrainerResponse result = trainerService.getById(1L);

        assertNotNull(result, "TrainerResponse should not be null");
        assertEquals(trainerResponse, result, "TrainerResponse should match the expected value");
    }

    @Test
    void testGetByIdNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class, () -> trainerService.getById(1L));

        assertEquals("Trainer with id 1 was not found", e.getMessage());
    }

    @Test
    void testGetByUsername() {
        when(trainerRepository.findByUserUsername("testUsername")).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toDto(trainerEntity)).thenReturn(trainerResponse);

        TrainerResponse result = trainerService.getByUsername("testUsername");

        assertNotNull(result, "TrainerResponse should not be null");
        assertEquals(trainerResponse, result, "TrainerResponse should match the expected value");
    }

    @Test
    void testGetByUsernameNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class,
                () -> trainerService.getByUsername("testUsername"));

        assertEquals("Trainer with username testUsername was not found", e.getMessage());
    }

    @Test
    void testGetTrainersNotAssignedToTrainee() {
        when(traineeRepository.existsByUserUsername("testTrainee")).thenReturn(true);
        when(trainerMapper.toDtoList(anyList())).thenReturn(List.of(trainerResponse));

        List<TrainerResponse> result = trainerService.getTrainersNotAssignedToTrainee("testTrainee");

        assertNotNull(result, "Trainer list should not be null");
        assertEquals(1, result.size(), "Should return one trainer");
        assertEquals(trainerResponse, result.get(0));
    }
}