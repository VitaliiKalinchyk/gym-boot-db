package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TraineeService;

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
class TraineeControllerImplTest {

    @InjectMocks
    private TraineeControllerImpl traineeController;

    @Mock
    private TraineeService traineeService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private LoggingService loggingService;

    private TraineeDto traineeDto;
    private UserCredentials credentials;

    private static final String TRAINEE_USERNAME = "Joe.Doe";

    @BeforeEach
    void setUp() {
        traineeDto = TraineeDto.builder().user(new UserDto()).build();
        credentials = new UserCredentials("Trainee", "password");
        setUpSecurityContext();
    }

    @Test
    void testGetTrainee() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(traineeDto);

        TraineeDto response = traineeController.get(TRAINEE_USERNAME);

        assertEquals(traineeDto, response);
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTraineeProfile() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(traineeDto);

        TraineeDto response = traineeController.get();

        assertEquals(traineeDto, response);
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testCreateTrainee() {
        when(traineeService.createProfile(traineeDto)).thenReturn(credentials);

        UserCredentials response = traineeController.create(traineeDto);

        assertEquals(credentials, response);
        verify(loggingService).logDebugController(anyString(), anyString());
    }

    @Test
    void testUpdateTrainee() {
        when(traineeService.update(traineeDto)).thenReturn(traineeDto);

        TraineeDto response = traineeController.update(traineeDto);

        assertEquals(traineeDto, response);
        assertEquals(TRAINEE_USERNAME, traineeDto.getUser().getUsername());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testDeleteTrainee() {
        assertDoesNotThrow(() -> traineeController.delete());

        verify(traineeService).deleteByUsername(TRAINEE_USERNAME);
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetUnassignedTrainers() {
        List<TrainerDto> trainers = List.of(TrainerDto.builder().id(1).build());
        when(traineeService.getTrainersNotAssignedToTrainee(TRAINEE_USERNAME)).thenReturn(trainers);

        List<TrainerDto> response = traineeController.getTrainersNotAssignedToTrainee();

        assertEquals(trainers, response);
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testUpdateTraineeTrainers() {
        assertDoesNotThrow(() -> traineeController.updateTraineeTrainers(1L));

        verify(traineeService).updateTraineeTrainers(TRAINEE_USERNAME, 1L);
        verify(loggingService).logDebugController(anyString());
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(TRAINEE_USERNAME);
    }
}
