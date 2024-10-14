package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.dto.UserCredentials;
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
    void getTraineeProfileTrainee() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(traineeDto);

        TraineeDto response = traineeController.getTraineeProfile(TRAINEE_USERNAME);

        assertEquals(traineeDto, response);
    }

    @Test
    void getTraineeProfileTraineeProfile() {
        when(traineeService.getByUsername(TRAINEE_USERNAME)).thenReturn(traineeDto);

        TraineeDto response = traineeController.getTraineeProfile();

        assertEquals(traineeDto, response);
    }

    @Test
    void createTraineeTrainee() {
        when(traineeService.createProfile(traineeDto)).thenReturn(credentials);

        UserCredentials response = traineeController.createTrainee(traineeDto);

        assertEquals(credentials, response);
    }

    @Test
    void updateTraineeProfileTrainee() {
        when(traineeService.update(traineeDto)).thenReturn(traineeDto);

        TraineeDto response = traineeController.updateTraineeProfile(traineeDto);

        assertEquals(traineeDto, response);
        assertEquals(TRAINEE_USERNAME, traineeDto.getUser().getUsername());
    }

    @Test
    void deleteTraineeTrainee() {
        assertDoesNotThrow(() -> traineeController.deleteTrainee());

        verify(traineeService).deleteByUsername(TRAINEE_USERNAME);
    }

    @Test
    void getTraineeProfileUnassignedTrainers() {
        List<TrainerDto> trainers = List.of(TrainerDto.builder().id(1).build());
        when(traineeService.getTrainersNotAssignedToTrainee(TRAINEE_USERNAME)).thenReturn(trainers);

        List<TrainerDto> response = traineeController.getTrainersNotAssignedToTrainee();

        assertEquals(trainers, response);
    }

    @Test
    void updateTraineeProfileTraineeTrainers() {
        assertDoesNotThrow(() -> traineeController.updateTraineeTrainers(1L));

        verify(traineeService).updateTraineeTrainers(TRAINEE_USERNAME, 1L);
    }

    private void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(TRAINEE_USERNAME);
    }
}
