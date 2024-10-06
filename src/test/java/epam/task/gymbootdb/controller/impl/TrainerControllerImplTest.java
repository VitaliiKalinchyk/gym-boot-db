package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TrainerService;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerImplTest {

    @InjectMocks
    private TrainerControllerImpl trainerController;

    @Mock
    private TrainerService trainerService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private LoggingService loggingService;

    private TrainerDto trainerDto;
    private UserCredentials credentials;

    private static final String TRAINER_USERNAME = "Joe.Doe";

    @BeforeEach
    void setUp() {
        trainerDto = TrainerDto.builder().user(new UserDto()).build();
        credentials = new UserCredentials("Trainee", "password");
        setUpSecurityContext();
    }

    @Test
    void testGetTrainer() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response = trainerController.get(TRAINER_USERNAME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trainerDto, response.getBody());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTrainerNoResponse() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainerController.get(TRAINER_USERNAME));
    }

    @Test
    void testGetTrainerProfile() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response = trainerController.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trainerDto, response.getBody());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTrainerProfileNoResponse() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainerController.get());
    }

    @Test
    void testCreateTrainer() {
        when(trainerService.createProfile(trainerDto)).thenReturn(credentials);

        ResponseEntity<UserCredentials> response = trainerController.create(trainerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(credentials, response.getBody());
        verify(loggingService).logDebugController(anyString(), anyString());
    }

    @Test
    void testCreateTrainerNoResponse() {
        when(trainerService.createProfile(trainerDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainerController.create(trainerDto));
    }

    @Test
    void testUpdateTrainer() {
        when(trainerService.update(trainerDto)).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response = trainerController.update(trainerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trainerDto, response.getBody());
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testUpdateTrainerNoResponse() {
        when(trainerService.update(trainerDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainerController.update(trainerDto));
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(TRAINER_USERNAME);
    }
}
