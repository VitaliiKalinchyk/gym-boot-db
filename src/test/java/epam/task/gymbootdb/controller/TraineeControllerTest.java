package epam.task.gymbootdb.controller;

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
class TraineeControllerTest {

    @InjectMocks
    private TraineeController traineeController;

    @Mock
    private TraineeService traineeService;

    private static final long traineeId = 1L;
    private static final TraineeDto traineeDto = TraineeDto.builder().id(1).build();
    private static final UserCredentials credentials = new UserCredentials("Trainee", "password");

    @Test
    void testGetTrainee() {
        when(traineeService.getById(traineeId)).thenReturn(traineeDto);

        ResponseEntity<TraineeDto> response = traineeController.get(traineeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(traineeDto, response.getBody());
    }

    @Test
    void testGetTraineeNoResponse() {
        when(traineeService.getById(traineeId)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.get(traineeId));
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

        ResponseEntity<TraineeDto> response = traineeController.update(traineeId, traineeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(traineeDto, response.getBody());
    }

    @Test
    void testUpdateTraineeNoResponse() {
        when(traineeService.update(traineeDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,() -> traineeController.update(traineeId, traineeDto));
    }

    @Test
    void testChangeActiveStatus() {
        ResponseEntity<Void> response = traineeController.changeActiveStatus(traineeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChangeActiveStatusNoResponse() {
        doThrow(new RuntimeException()).when(traineeService).changeStatus(traineeId);

        assertThrows(RuntimeException.class, () -> traineeController.changeActiveStatus(traineeId));
    }

    @Test
    void testDeleteTrainee() {
        ResponseEntity<Void> response = traineeController.delete(traineeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteTraineeNoResponse() {
        doThrow(new RuntimeException()).when(traineeService).deleteById(traineeId);

        assertThrows(RuntimeException.class, () -> traineeController.delete(traineeId));
    }

    @Test
    void testGetUnassignedTrainers() {
        List<TrainerDto> trainers = List.of(TrainerDto.builder().id(1).build());
        when(traineeService.getTrainersNotAssignedToTrainee(traineeId)).thenReturn(trainers);

        ResponseEntity<List<TrainerDto>> response = traineeController.getTrainersNotAssignedToTrainee(traineeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trainers, response.getBody());
    }

    @Test
    void testGetUnassignedTrainersNoResponse() {
        when(traineeService.getTrainersNotAssignedToTrainee(traineeId)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> traineeController.getTrainersNotAssignedToTrainee(traineeId));
    }

    @Test
    void testUpdateTraineeTrainers() {
        ResponseEntity<Void> response = traineeController.updateTraineeTrainers(traineeId, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTraineeTrainersNoResponse() {
        doThrow(new RuntimeException()).when(traineeService).updateTraineeTrainers(traineeId, 2L);

        assertThrows(RuntimeException.class, () -> traineeController.updateTraineeTrainers(traineeId, 2L));
    }
}
