package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.service.TrainingService;

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
class TrainingControllerTest {

    @InjectMocks
    private TrainingController trainingController;

    @Mock
    private TrainingService trainingService;

    private static final TrainingDto trainingDto = TrainingDto.builder().id(1).build();
    private static final long TRAINEE_ID = 1L;
    private static final long TRAINER_ID = 2L;

    @Test
    void testCreateTraining() {
        ResponseEntity<Void> response = trainingController.create(trainingDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateTrainingNoResponse() {
        doThrow(new RuntimeException()).when(trainingService).create(trainingDto);

        assertThrows(RuntimeException.class, () -> trainingController.create(trainingDto));
    }

    @Test
    void testGetTraineeTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);
        TraineeTrainingsRequest request = TraineeTrainingsRequest.builder()
                .traineeId(TRAINEE_ID)
                .build();

        when(trainingService.getTraineeTrainings(request)).thenReturn(trainings);

        ResponseEntity<List<TrainingDto>> response = trainingController.getTraineeTrainings(TRAINEE_ID,
                null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(trainingDto, response.getBody().get(0));
    }

    @Test
    void testGetTraineeTrainingsNoResponse() {
        when(trainingService.getTraineeTrainings(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> trainingController.getTraineeTrainings(TRAINEE_ID,
                null, null, null, null));
    }

    @Test
    void testGetTrainerTrainings() {
        List<TrainingDto> trainings = List.of(trainingDto);
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder()
                .trainerId(TRAINER_ID)
                .build();

        when(trainingService.getTrainerTrainings(request)).thenReturn(trainings);

        ResponseEntity<List<TrainingDto>> response =
                trainingController.getTrainerTrainings(TRAINER_ID, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(trainingDto, response.getBody().get(0));
    }

    @Test
    void testGetTrainerTrainingsNoResponse() {
        when(trainingService.getTrainerTrainings(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> trainingController.getTrainerTrainings(TRAINER_ID, null, null, null));
    }
}
