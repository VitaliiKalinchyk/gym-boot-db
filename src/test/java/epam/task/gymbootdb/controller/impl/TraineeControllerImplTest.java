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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        ResponseEntity<TraineeDto> response = traineeController.get(TRAINEE_USERNAME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(traineeDto, response.getBody());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTraineeNoResponse() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.get(TRAINEE_USERNAME));
    }

    @Test
    void testGetTraineeProfile() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response = traineeController.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(traineeDto, response.getBody());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTraineeProfileNoResponse() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.get());
    }

    @Test
    void testCreateTrainee() {
        when(traineeService.createProfile(traineeDto)).thenReturn(credentials);

        ResponseEntity<UserCredentials> response = traineeController.create(traineeDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(credentials, response.getBody());
        verify(loggingService).logDebugController(anyString(), anyString());
    }

    @Test
    void testCreateTraineeNoResponse() {
        when(traineeService.createProfile(traineeDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.create(traineeDto));
    }

    @Test
    void testUpdateTrainee() {
        when(traineeService.update(traineeDto)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response = traineeController.update(traineeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(traineeDto, response.getBody());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testUpdateTraineeNoResponse() {
        when(traineeService.update(traineeDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.update(traineeDto));
    }

    @Test
    void testDeleteTrainee() {
        ResponseEntity<Void> response = traineeController.delete();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testDeleteTraineeNoResponse() {
        doThrow(new RuntimeException()).when(traineeService).deleteByUsername(TRAINEE_USERNAME);

        assertThrows(RuntimeException.class, () -> traineeController.delete());
    }

    @Test
    void testGetUnassignedTrainers() {
        List<TrainerDto> trainers = List.of(TrainerDto.builder().id(1).build());
        when(traineeService.getTrainersNotAssignedToTrainee(TRAINEE_USERNAME)).thenReturn(trainers);

        ResponseEntity<List<TrainerDto>> response = traineeController.getTrainersNotAssignedToTrainee();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trainers, response.getBody());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetUnassignedTrainersNoResponse() {
        when(traineeService.getTrainersNotAssignedToTrainee(TRAINEE_USERNAME)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.getTrainersNotAssignedToTrainee());
    }

    @Test
    void testUpdateTraineeTrainers() {
        ResponseEntity<Void> response = traineeController.updateTraineeTrainers(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testUpdateTraineeTrainersNoResponse() {
        doThrow(new RuntimeException()).when(traineeService).updateTraineeTrainers(TRAINEE_USERNAME, 1L);

        assertThrows(RuntimeException.class, () -> traineeController.updateTraineeTrainers(1L));
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(TRAINEE_USERNAME);
    }
}
