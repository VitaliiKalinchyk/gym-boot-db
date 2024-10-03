package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.exception.TrainingTypeException;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingTypeRepository;
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
    private UserRepository userRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
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

    @BeforeEach
    void setUp() {
        user = User.builder().firstName("Joe").lastName("Doe").build();
        TrainingTypeDto trainingType = TrainingTypeDto.builder().id(1).build();
        trainerRequest = TrainerDto.builder().trainingType(trainingType).build();
        trainerEntity = new Trainer();
        trainerEntity.setUser(user);
        trainerResponse = new TrainerDto();
    }

    @Test
    void testCreateProfile() {
        when(trainingTypeRepository.existsById(anyLong())).thenReturn(true);
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
    void testCreateProfileNoSuchTrainingType() {
        when(trainingTypeRepository.existsById(anyLong())).thenReturn(false);

        TrainingTypeException e = assertThrows(TrainingTypeException.class,
                () -> trainerService.createProfile(trainerRequest));
        assertEquals("TrainingType with id 1 does not exist", e.getReason());
    }

    @Test
    void testCreateProfileUsernameExists() {
        when(trainingTypeRepository.existsById(anyLong())).thenReturn(true);
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
        assertEquals(trainerRequest.getUser().getFirstName(), trainerEntity.getUser().getFirstName());
        assertEquals(trainerRequest.getUser().getLastName(), trainerEntity.getUser().getLastName());
        assertEquals(trainerRequest.getUser().isActive(), trainerEntity.getUser().isActive());
        assertEquals(1, result.getTrainees().size());
    }

    @Test
    void testUpdateTrainerNoSuchTrainer() {
        TrainerException e = assertThrows(TrainerException.class, () -> trainerService.update(trainerRequest));

        assertEquals("Trainer with id 0 was not found", e.getReason());
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

        assertEquals("Trainer with id 1 was not found", e.getReason());
    }
}
