package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TraineeService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerImplTest {

    @InjectMocks
    private TraineeControllerImpl traineeController;

    @Mock
    private TraineeService traineeService;

    private static final long TRAINEE_ID = 1L;
    private static final TraineeDto traineeDto = TraineeDto.builder().id(1).build();
    private static final UserCredentials credentials = new UserCredentials("Trainee", "password");

    @Test
    void testGetTrainee() {
        when(traineeService.getById(TRAINEE_ID)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response = traineeController.get(TRAINEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(traineeDto, response.getBody());
    }

    @Test
    void testGetTraineeNoResponse() {
        when(traineeService.getById(TRAINEE_ID)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.get(TRAINEE_ID));
    }

    @Test
    void testCreateTrainee() {
        when(traineeService.createProfile(traineeDto)).thenReturn(credentials);

        ResponseEntity<UserCredentials> response = traineeController.create(traineeDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(credentials, response.getBody());
    }

    @Test
    void testCreateTraineeNoResponse() {
        when(traineeService.createProfile(traineeDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,() -> traineeController.create(traineeDto));
    }

    @Test
    void testUpdateTrainee() {
        when(traineeService.update(traineeDto)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response = traineeController.update(TRAINEE_ID, traineeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(traineeDto, response.getBody());
    }

    @Test
    void testUpdateTraineeNoResponse() {
        when(traineeService.update(traineeDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,() -> traineeController.update(TRAINEE_ID, traineeDto));
    }

    @Test
    void testDeleteTrainee() {
        ResponseEntity<Void> response = traineeController.delete(TRAINEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteTraineeNoResponse() {
        doThrow(new RuntimeException()).when(traineeService).deleteById(TRAINEE_ID);

        assertThrows(RuntimeException.class, () -> traineeController.delete(TRAINEE_ID));
    }

    @Test
    void testGetUnassignedTrainers() {
        List<TrainerDto> trainers = List.of(TrainerDto.builder().id(1).build());
        when(traineeService.getTrainersNotAssignedToTrainee(TRAINEE_ID)).thenReturn(trainers);

        ResponseEntity<List<TrainerDto>> response = traineeController.getTrainersNotAssignedToTrainee(TRAINEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trainers, response.getBody());
    }

    @Test
    void testGetUnassignedTrainersNoResponse() {
        when(traineeService.getTrainersNotAssignedToTrainee(TRAINEE_ID)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.getTrainersNotAssignedToTrainee(TRAINEE_ID));
    }

    @Test
    void testUpdateTraineeTrainers() {
        ResponseEntity<Void> response = traineeController.updateTraineeTrainers(TRAINEE_ID, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTraineeTrainersNoResponse() {
        doThrow(new RuntimeException()).when(traineeService).updateTraineeTrainers(TRAINEE_ID, 2L);

        assertThrows(RuntimeException.class, () -> traineeController.updateTraineeTrainers(TRAINEE_ID, 2L));
    }
}
