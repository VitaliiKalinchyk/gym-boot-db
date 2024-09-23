package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TrainerService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @InjectMocks
    private TrainerController trainerController;

    @Mock
    private TrainerService trainerService;

    private static final long TRAINER_ID = 1L;
    private static final TrainerDto trainerDto = TrainerDto.builder().id(1).build();
    private static final UserCredentials userCredentials = new UserCredentials("Trainer", "password");

    @Test
    void testGetTrainer() {
        when(trainerService.getById(TRAINER_ID)).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response = trainerController.get(TRAINER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trainerDto, response.getBody());
    }

    @Test
    void testGetTrainerNoResponse() {
        when(trainerService.getById(TRAINER_ID)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainerController.get(TRAINER_ID));
    }

    @Test
    void testCreateTrainer() {
        when(trainerService.createProfile(trainerDto)).thenReturn(userCredentials);

        ResponseEntity<UserCredentials> response = trainerController.create(trainerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userCredentials, response.getBody());
    }

    @Test
    void testCreateTrainerNoResponse() {
        when(trainerService.createProfile(trainerDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainerController.create(trainerDto));
    }

    @Test
    void testUpdateTrainer() {
        when(trainerService.update(trainerDto)).thenReturn(trainerDto);

        ResponseEntity<TrainerDto> response = trainerController.update(TRAINER_ID, trainerDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trainerDto, response.getBody());
    }

    @Test
    void testUpdateTrainerNoResponse() {
        when(trainerService.update(trainerDto)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainerController.update(TRAINER_ID, trainerDto));
    }

    @Test
    void testChangeActiveStatus() {
        ResponseEntity<Void> response = trainerController.changeActiveStatus(TRAINER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChangeActiveStatusNoResponse() {
        doThrow(new RuntimeException()).when(trainerService).setActiveStatus(TRAINER_ID);

        assertThrows(RuntimeException.class, () -> trainerController.changeActiveStatus(TRAINER_ID));
    }
}
