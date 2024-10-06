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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private static final TrainingDto trainingDto = TrainingDto.builder().id(1).build();
    private static final String USERNAME = "Joe";

    @BeforeEach
    void setUp() {
        setUpSecurityContext();
    }

    @Test
    void testCreateTraining() {
        ResponseEntity<Void> response = trainingController.create(trainingDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testCreateTrainingNoResponse() {
        doThrow(new RuntimeException()).when(trainingService).create(trainingDto);

        assertThrows(RuntimeException.class, () -> trainingController.create(trainingDto));
    }

    @Test
    void testGetTraineeTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTraineeTrainings(any())).thenReturn(trainings);

        ResponseEntity<List<TrainingDto>> response = trainingController.getTraineeTrainings(null, null,
                null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(trainingDto, response.getBody().getFirst());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTraineeTrainingsNoResponse() {
        when(trainingService.getTraineeTrainings(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainingController.getTraineeTrainings(null, null,
                null, null));
    }

    @Test
    void testGetTrainerTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);

        when(trainingService.getTrainerTrainings(any())).thenReturn(trainings);

        ResponseEntity<List<TrainingDto>> response =
                trainingController.getTrainerTrainings(null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(trainingDto, response.getBody().getFirst());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTrainerTrainingsNoResponse() {
        when(trainingService.getTrainerTrainings(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> trainingController.getTrainerTrainings(null, null, null));
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(USERNAME);
    }
}
