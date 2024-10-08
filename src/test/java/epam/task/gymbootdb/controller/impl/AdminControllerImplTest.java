package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerImplTest {

    @InjectMocks
    private AdminControllerImpl adminController;
    @Mock
    private UserService userService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private LoggingService loggingService;

    private TraineeDto traineeDto;
    private TrainerDto trainerDto;
    private TrainingDto trainingDto;

    private static final String USERNAME = "Joe.Doe";


    @BeforeEach
    void setUp() {
        UserDto user = new UserDto();
        traineeDto = TraineeDto.builder().user(user).build();
        trainerDto = TrainerDto.builder().user(user).build();
        trainingDto = TrainingDto.builder().id(1).trainee(traineeDto).build();
    }

    @Test
    void testChangeActiveStatus() {
        assertDoesNotThrow(() -> adminController.changeActiveStatus(USERNAME));
        verify(userService).changeStatus(USERNAME);
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }

    @Test
    void testUpdateTrainee() {
        when(traineeService.update(traineeDto)).thenReturn(traineeDto);

        TraineeDto response = adminController.updateTrainee(USERNAME, traineeDto);

        assertEquals(traineeDto, response);
        assertEquals(USERNAME, traineeDto.getUser().getUsername());
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }

    @Test
    void testDeleteTrainee() {
        assertDoesNotThrow(() -> adminController.deleteTrainee(USERNAME));

        verify(traineeService).deleteByUsername(USERNAME);
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }

    @Test
    void testGetTrainersNotAssignedToTrainee() {
        List<TrainerDto> trainers = List.of(TrainerDto.builder().id(1).build());
        when(traineeService.getTrainersNotAssignedToTrainee(USERNAME)).thenReturn(trainers);

        List<TrainerDto> response = adminController.getTrainersNotAssignedToTrainee(USERNAME);

        assertEquals(trainers, response);
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }

    @Test
    void testUpdateTraineeTrainers() {
        assertDoesNotThrow(() -> adminController.updateTraineeTrainers(USERNAME, 1L));

        verify(traineeService).updateTraineeTrainers(USERNAME, 1L);
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }

    @Test
    void testUpdateTrainer() {
        when(trainerService.update(trainerDto)).thenReturn(trainerDto);

        TrainerDto response = adminController.updateTrainer(USERNAME, trainerDto);

        assertEquals(trainerDto, response);
        assertEquals(USERNAME, trainerDto.getUser().getUsername());
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }

    @Test
    void testCreateTraining() {
        assertDoesNotThrow(() -> adminController.createTraining(USERNAME, trainingDto));
        assertEquals(USERNAME, trainingDto.getTrainee().getUser().getUsername());
        verify(trainingService).create(trainingDto);
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }

    @Test
    void testGetTraineeTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTraineeTrainings(any())).thenReturn(trainings);

        List<TrainingDto> response = adminController.getTraineeTrainings(USERNAME, null, null,
                null, null);

        assertEquals(1, response.size());
        assertEquals(trainingDto, response.getFirst());
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }

    @Test
    void testGetTrainerTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTrainerTrainings(any())).thenReturn(trainings);

        List<TrainingDto> response = adminController.getTrainerTrainings(USERNAME, null, null, null);

        assertEquals(1, response.size());
        assertEquals(trainingDto, response.getFirst());
        verify(loggingService).logDebugAdminController(anyString(), anyString());
    }
}