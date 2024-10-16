package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.service.TrainerService;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerImplTest {

    private static final String TRAINER_USERNAME = "Joe.Doe";

    @Mock
    private TrainerService trainerService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TrainerControllerImpl trainerController;

    private TrainerDto trainerDto;
    private UserCredentials credentials;

    @BeforeEach
    void setUp() {
        trainerDto = TrainerDto.builder().user(new UserDto()).build();
        credentials = new UserCredentials("Trainee", "password");
        setUpSecurityContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getTrainerProfileTrainer() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(trainerDto);

        TrainerDto response = trainerController.getTrainerProfile(TRAINER_USERNAME);

        assertEquals(trainerDto, response);
    }

    @Test
    void getTrainerProfileTrainerProfile() {
        when(trainerService.getByUsername(TRAINER_USERNAME)).thenReturn(trainerDto);

        TrainerDto response = trainerController.getTrainerProfile();

        assertEquals(trainerDto, response);
    }

    @Test
    void createTrainerTrainer() {
        when(trainerService.createProfile(trainerDto)).thenReturn(credentials);

        UserCredentials response = trainerController.createTrainer(trainerDto);

        assertEquals(credentials, response);
    }

    @Test
    void updateTrainerProfileTrainer() {
        when(trainerService.update(trainerDto)).thenReturn(trainerDto);

        TrainerDto response = trainerController.updateTrainerProfile(trainerDto);

        assertEquals(trainerDto, response);
        assertEquals(TRAINER_USERNAME, trainerDto.getUser().getUsername());
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(TRAINER_USERNAME);
    }
}
