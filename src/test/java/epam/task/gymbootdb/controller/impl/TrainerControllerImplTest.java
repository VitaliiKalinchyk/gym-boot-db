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

        TrainerDto response = trainerController.get(TRAINER_USERNAME);

        assertEquals(trainerDto, response);
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testGetTrainerProfile() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(trainerDto);

        TrainerDto response = trainerController.get();

        assertEquals(trainerDto, response);
        verify(loggingService).logDebugController(anyString());
    }

    @Test
    void testCreateTrainer() {
        when(trainerService.createProfile(trainerDto)).thenReturn(credentials);

        UserCredentials response = trainerController.create(trainerDto);

        assertEquals(credentials, response);
        verify(loggingService).logDebugController(anyString(), anyString());
    }

    @Test
    void testUpdateTrainer() {
        when(trainerService.update(trainerDto)).thenReturn(trainerDto);

        TrainerDto response = trainerController.update(trainerDto);

        assertEquals(trainerDto, response);
        assertEquals(TRAINER_USERNAME, trainerDto.getUser().getUsername());
        verify(loggingService).logDebugController(anyString());
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(TRAINER_USERNAME);
    }
}
