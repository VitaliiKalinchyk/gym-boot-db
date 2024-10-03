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
        user = User.builder().firstName("Joe").lastName("Smith").build();
        traineeRequest = new TraineeDto();
        traineeResponse = new TraineeDto();
        traineeEntity = new Trainee();
        traineeEntity.setUser(user);
    }

    @Test
    void testCreateProfile() {
        when(traineeMapper.toEntity(traineeRequest)).thenReturn(traineeEntity);
        when(passwordGenerator.generatePassword()).thenReturn("testPassword");
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn("testUsername");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserCredentials result = traineeService.createProfile(traineeRequest);

        assertNotNull(result, "User credentials should not be null");
        assertEquals("testUsername", result.getUsername(), "Username should match");
        assertEquals("testPassword", result.getPassword(), "Password should match");
        assertTrue(user.isActive(), "User should be active");
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
    }

    @Test
    void testCreateProfileUsernameExists() {
        when(traineeMapper.toEntity(traineeRequest)).thenReturn(traineeEntity);
        when(passwordGenerator.generatePassword()).thenReturn("testPassword");
        when(nameGenerator.generateUsername(anyString(), anyString())).thenReturn("testUsername");
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        when(userRepository.findUsernamesByUsernameStartsWith("testUsername")).thenReturn(List.of("testUsername"));
        when(nameGenerator.generateUsername("testUsername", List.of("testUsername")))
                .thenReturn("testUsername1");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserCredentials result = traineeService.createProfile(traineeRequest);

        assertNotNull(result, "User credentials should not be null");
        assertEquals("testUsername1", result.getUsername(), "Username should match");
        assertEquals("testPassword", result.getPassword(), "Password should match");
        assertTrue(user.isActive(), "User should be active");
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
    }

    @Test
    void testUpdateTrainee() {
        traineeRequest.setBirthday(LocalDate.now());
        traineeRequest.setAddress("address");
        traineeRequest.setUser(UserDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .active(false).build());

        when(traineeRepository.findById(traineeRequest.getId())).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toDto(any())).thenReturn(traineeResponse);
        when(trainerMapper.toDtoList(any())).thenReturn(List.of(new TrainerDto()));

        TraineeDto result = traineeService.update(traineeRequest);

        assertNotNull(result, "TraineeResponse should not be null");
        assertEquals(traineeResponse, result, "TraineeResponse should match the expected value");
        assertEquals(traineeRequest.getBirthday(), traineeEntity.getBirthday());
        assertEquals(traineeRequest.getAddress(), traineeEntity.getAddress());
        assertEquals(traineeRequest.getUser().getFirstName(), traineeEntity.getUser().getFirstName());
        assertEquals(traineeRequest.getUser().getLastName(), traineeEntity.getUser().getLastName());
        assertEquals(traineeRequest.getUser().isActive(), traineeEntity.getUser().isActive());
        assertEquals(1, result.getTrainers().size());
    }

    @Test
    void testUpdateTraineeNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.update(traineeRequest));

        assertEquals("Trainee with id 0 was not found", e.getReason());
    }

    @Test
    void testGetById() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toDto(traineeEntity)).thenReturn(traineeResponse);
        when(trainerMapper.toDtoList(any())).thenReturn(List.of(new TrainerDto()));

        TraineeDto result = traineeService.getById(1L);

        assertNotNull(result, "TraineeResponse should not be null");
        assertEquals(traineeResponse, result, "TraineeResponse should match the expected value");
        assertEquals(1, traineeResponse.getTrainers().size());
    }

    @Test
    void testGetByIdNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class, () -> traineeService.getById(1L));

        assertEquals("Trainee with id 1 was not found", e.getReason());
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
        assertEquals("Trainee with id 1 was not found", e.getReason());
    }

    @Test
    void testUpdateTraineeTrainers() {
        traineeEntity.setTrainers(new ArrayList<>());
        Trainer trainer = new Trainer();

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));

        traineeService.updateTraineeTrainers(1L, 2L);

        assertEquals(1, traineeEntity.getTrainers().size());
        assertTrue(traineeEntity.getTrainers().contains(trainer));
    }

    @Test
    void testUpdateTraineeTrainersTrainerAlreadyInList () {
        Trainer trainer = new Trainer();
        traineeEntity.setTrainers(List.of(trainer));

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));

        traineeService.updateTraineeTrainers(1L, 2L);

        assertEquals(1, traineeEntity.getTrainers().size());
        verify(traineeRepository, never()).save(any(Trainee.class));
    }

    @Test
    void testUpdateTraineeTrainersNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.updateTraineeTrainers(1L, 2L));
        assertEquals("Trainee with id 1 was not found", e.getReason());
    }

    @Test
    void testUpdateTraineeTrainersNoSuchTrainer() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(traineeEntity));

        TrainerException e = assertThrows(TrainerException.class,
                () -> traineeService.updateTraineeTrainers(1L, 2L));
        assertEquals("Trainer with id 2 was not found", e.getReason());
    }


    @Test
    void testGetTrainersNotAssignedToTrainee() {
        TrainerDto trainerDto = TrainerDto.builder().id(1L).build();

        when(traineeRepository.existsById(1L)).thenReturn(true);
        when(trainerMapper.toDtoList(anyList())).thenReturn(List.of(trainerDto));

        List<TrainerDto> result = traineeService.getTrainersNotAssignedToTrainee(1L);

        assertNotNull(result, "Trainer list should not be null");
        assertEquals(1, result.size(), "Should return one trainer");
        assertEquals(trainerDto, result.getFirst());
    }

    @Test
    void testGetTrainersNotAssignedToTraineeNoSuchTrainee() {
        TraineeException e = assertThrows(TraineeException.class,
                () -> traineeService.getTrainersNotAssignedToTrainee(1L));

        assertEquals("Trainee with id 1 was not found", e.getReason());
    }
}
