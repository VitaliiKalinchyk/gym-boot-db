package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.service.UserService;
import epam.task.gymbootdb.service.TrainerService;
import epam.task.gymbootdb.service.TraineeService;
import epam.task.gymbootdb.service.TrainingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void testChangeActiveStatusForUserByAdmin() {
        assertDoesNotThrow(() -> adminController.changeActiveStatusForUserByAdmin(USERNAME));
        verify(userService).changeStatus(USERNAME);
    }

    @Test
    void testUpdateTraineeProfileByAdmin() {
        when(traineeService.update(traineeDto)).thenReturn(traineeDto);

        TraineeDto response = adminController.updateTraineeProfileByAdmin(USERNAME, traineeDto);

        assertEquals(traineeDto, response);
        assertEquals(USERNAME, traineeDto.getUser().getUsername());
    }

    @Test
    void testDeleteTraineeProfileByAdmin() {
        assertDoesNotThrow(() -> adminController.deleteTraineeProfileByAdmin(USERNAME));

        verify(traineeService).deleteByUsername(USERNAME);
    }

    @Test
    void testGetTrainersNotAssignedToTraineeByAdmin() {
        List<TrainerDto> trainers = List.of(TrainerDto.builder().id(1).build());
        when(traineeService.getTrainersNotAssignedToTrainee(USERNAME)).thenReturn(trainers);

        List<TrainerDto> response = adminController.getTrainersNotAssignedToTraineeByAdmin(USERNAME);

        assertEquals(trainers, response);
    }

    @Test
    void testUpdateTraineeProfileByAdminTrainers() {
        assertDoesNotThrow(() -> adminController.updateTraineeTrainersByAdmin(USERNAME, 1L));

        verify(traineeService).updateTraineeTrainers(USERNAME, 1L);
    }

    @Test
    void testUpdateTrainerProfileByAdmin() {
        when(trainerService.update(trainerDto)).thenReturn(trainerDto);

        TrainerDto response = adminController.updateTrainerProfileByAdmin(USERNAME, trainerDto);

        assertEquals(trainerDto, response);
        assertEquals(USERNAME, trainerDto.getUser().getUsername());
    }

    @Test
    void testCreateTrainingFoTraineeByAdmin() {
        assertDoesNotThrow(() -> adminController.createTrainingFoTraineeByAdmin(USERNAME, trainingDto));
        assertEquals(USERNAME, trainingDto.getTrainee().getUser().getUsername());
        verify(trainingService).create(trainingDto);
    }

    @Test
    void testGetTraineeTrainingsByAdmin() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTraineeTrainings(any())).thenReturn(trainings);

        List<TrainingDto> response = adminController.getTraineeTrainingsByAdmin(USERNAME, null, null,
                null, null);

        assertEquals(1, response.size());
        assertEquals(trainingDto, response.getFirst());
    }

    @Test
    void testGetTrainerTrainingsByAdmin() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTrainerTrainings(any())).thenReturn(trainings);

        List<TrainingDto> response = adminController.getTrainerTrainingsByAdmin(USERNAME, null, null, null);

        assertEquals(1, response.size());
        assertEquals(trainingDto, response.getFirst());
    }
}