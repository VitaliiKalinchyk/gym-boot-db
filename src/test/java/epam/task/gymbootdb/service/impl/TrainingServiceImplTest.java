package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.dto.mapper.TrainingMapper;
import epam.task.gymbootdb.entity.Training;
import epam.task.gymbootdb.exception.TraineeException;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private TrainingDto trainingRequest;
    private Training trainingEntity;
    private TrainingDto trainingResponse;

    @BeforeEach
    void setUp() {
        trainingRequest = new TrainingDto();
        trainingEntity = new Training();
        trainingResponse = new TrainingDto();
    }

    @Test
    void testCreate() {
        when(trainingMapper.toEntity(trainingRequest)).thenReturn(trainingEntity);

        trainingService.create(trainingRequest);

        verify(trainingRepository).save(trainingEntity);
    }

    @Test
    void testGetTraineeTrainings() {
        TraineeTrainingsRequest request = TraineeTrainingsRequest.builder().traineeId(1L).build();

        when(traineeRepository.existsById(1L)).thenReturn(true);
        when(trainingRepository.findTraineeTrainingsByOptionalParams(anyLong(), any(), any(), any(), any()))
                .thenReturn(List.of(trainingEntity));
        when(trainingMapper.toDtoList(anyList())).thenReturn(List.of(trainingResponse));

        List<TrainingDto> result = trainingService.getTraineeTrainings(request);

        assertNotNull(result, "Trainee trainings list should not be null");
        assertEquals(1, result.size(), "Expected one training");
        assertEquals(trainingResponse, result.get(0), "Returned training should match the expected value");
    }

    @Test
    void testGetTraineeTrainingsNoSuchTrainee() {
        TraineeTrainingsRequest request = TraineeTrainingsRequest.builder().traineeId(1L).build();

        when(traineeRepository.existsById(1L)).thenReturn(false);

        TraineeException e = assertThrows(TraineeException.class, () -> trainingService.getTraineeTrainings(request));
        assertEquals("Trainee with id 1 was not found", e.getReason());
    }

    @Test
    void testGetTrainerTrainings() {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder().trainerId(1L).build();

        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(trainingRepository.findTrainerTrainingsByOptionalParams(anyLong(), any(), any(), any()))
                .thenReturn(List.of(trainingEntity));
        when(trainingMapper.toDtoList(anyList())).thenReturn(List.of(trainingResponse));

        List<TrainingDto> result = trainingService.getTrainerTrainings(request);

        assertNotNull(result, "Trainer trainings list should not be null");
        assertEquals(1, result.size(), "Expected one training");
        assertEquals(trainingResponse, result.get(0), "Returned training should match the expected value");
    }

    @Test
    void testGetTrainerTrainingsNoSuchTrainer() {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder().trainerId(1L).build();

        when(trainerRepository.existsById(1L)).thenReturn(false);

        TrainerException e = assertThrows(TrainerException.class, () -> trainingService.getTrainerTrainings(request));
        assertEquals("Trainer with id 1 was not found", e.getReason());
    }
}
