package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.service.TrainingService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingControllerImplTest {

    private static final String USERNAME = "Joe";

    @Mock
    private TrainingService trainingService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TrainingControllerImpl trainingController;

    private TrainingDto trainingDto;

    @BeforeEach
    void setUp() {
        TraineeDto traineeDto = TraineeDto.builder().user(new UserDto()).build();
        trainingDto = TrainingDto.builder().id(1).trainee(traineeDto).build();
        setUpSecurityContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTrainingTraining() {
        assertDoesNotThrow(() -> trainingController.createTraining(trainingDto));
        assertEquals(USERNAME, trainingDto.getTrainee().getUser().getUsername());
        verify(trainingService).create(trainingDto);
    }

    @Test
    void getTraineeTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTraineeTrainings(any())).thenReturn(trainings);

        List<TrainingDto> response = trainingController.getTraineeTrainings(null, null,
                null, null);

        assertEquals(1, response.size());
        assertEquals(trainingDto, response.getFirst());
    }

    @Test
    void getTrainerTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTrainerTrainings(any())).thenReturn(trainings);

        List<TrainingDto> response = trainingController.getTrainerTrainings(null, null, null);

        assertEquals(1, response.size());
        assertEquals(trainingDto, response.getFirst());
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(USERNAME);
    }
}
