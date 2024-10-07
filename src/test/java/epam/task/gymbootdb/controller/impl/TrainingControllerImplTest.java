package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TrainingService;

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

    @InjectMocks
    private TrainingControllerImpl trainingController;

    @Mock
    private TrainingService trainingService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private LoggingService loggingService;

    private TrainingDto trainingDto;
    private static final String USERNAME = "Joe";

    @BeforeEach
    void setUp() {
        TraineeDto traineeDto = TraineeDto.builder().user(new UserDto()).build();
        trainingDto = TrainingDto.builder().id(1).trainee(traineeDto).build();
        setUpSecurityContext();
    }

    @Test
    void testCreateTraining() {
        assertDoesNotThrow(() -> trainingController.create(trainingDto));
        assertEquals(USERNAME, trainingDto.getTrainee().getUser().getUsername());
        verify(trainingService).create(trainingDto);
        verify(loggingService).logDebugController(anyString(),anyString());
    }

    @Test
    void testGetTraineeTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTraineeTrainings(any())).thenReturn(trainings);

        List<TrainingDto> response = trainingController.getTraineeTrainings(null, null,
                null, null);

        assertEquals(1, response.size());
        assertEquals(trainingDto, response.getFirst());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTrainerTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTrainerTrainings(any())).thenReturn(trainings);

        List<TrainingDto> response = trainingController.getTrainerTrainings(null, null, null);

        assertEquals(1, response.size());
        assertEquals(trainingDto, response.getFirst());
        verify(loggingService).logDebugController(anyString());
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(USERNAME);
    }
}
