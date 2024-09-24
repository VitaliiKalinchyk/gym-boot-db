package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TrainingMapper;
import epam.task.gymbootdb.entity.Training;
import epam.task.gymbootdb.exception.TraineeException;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.exception.TrainingTypeException;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingRepository;

import epam.task.gymbootdb.repository.TrainingTypeRepository;
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
    private TrainingTypeRepository trainingTypeRepository;
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
    void testCreateTraining() {
        buildRequest();

        when(traineeRepository.existsById(1L)).thenReturn(true);
        when(trainerRepository.existsById(1L)).thenReturn(true);
        when(trainingTypeRepository.existsById(1L)).thenReturn(true);
        when(trainingMapper.toEntity(trainingRequest)).thenReturn(trainingEntity);

        trainingService.create(trainingRequest);

        verify(trainingRepository).save(trainingEntity);
    }

    @Test
    void testCreateTrainingNoSuchTrainee() {
        buildRequest();

        TraineeException e = assertThrows(TraineeException.class, () -> trainingService.create(trainingRequest));
        assertEquals("Trainee with id 1 was not found", e.getReason());
    }

    @Test
    void testCreateTrainingNoSuchTrainer() {
        buildRequest();

        when(traineeRepository.existsById(1L)).thenReturn(true);

        TrainerException e = assertThrows(TrainerException.class, () -> trainingService.create(trainingRequest));

        assertEquals("Trainer with id 1 was not found", e.getReason());
    }

    @Test
    void testCreateTrainingNoSuchTrainingType() {
        buildRequest();

        when(traineeRepository.existsById(1L)).thenReturn(true);
        when(trainerRepository.existsById(1L)).thenReturn(true);

        TrainingTypeException e = assertThrows(TrainingTypeException.class,
                () -> trainingService.create(trainingRequest));
        assertEquals("TrainingType with id 1 does not exist", e.getReason());
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

        TrainerException e = assertThrows(TrainerException.class, () -> trainingService.getTrainerTrainings(request));
        assertEquals("Trainer with id 1 was not found", e.getReason());
    }

    private void buildRequest() {
        trainingRequest.setTrainee(TraineeDto.builder().id(1).build());
        trainingRequest.setTrainer(TrainerDto.builder().id(1).build());
        trainingRequest.setTrainingType(TrainingTypeDto.builder().id(1).build());
    }
}
