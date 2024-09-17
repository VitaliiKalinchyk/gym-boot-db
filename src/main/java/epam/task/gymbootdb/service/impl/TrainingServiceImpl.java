package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingCreateRequest;
import epam.task.gymbootdb.dto.TrainingResponse;
import epam.task.gymbootdb.dto.mapper.TrainingMapper;
import epam.task.gymbootdb.entity.Training;
import epam.task.gymbootdb.exception.TraineeException;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingRepository;
import epam.task.gymbootdb.service.TrainingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingMapper trainingMapper;

    @Override
    public TrainingResponse create(TrainingCreateRequest request) {
        Training entity = trainingMapper.toEntity(request);
        Training savedEntity = trainingRepository.save(entity);
        log.debug("Training created with ID: {}", savedEntity.getId());

        return trainingMapper.toDto(savedEntity);    }

    @Override
    public List<TrainingResponse> getTraineeTrainings(TraineeTrainingsRequest request) {
        String traineeUsername = request.getTraineeUsername();
        if (!traineeRepository.existsByUserUsername(traineeUsername)) throw new TraineeException(traineeUsername);
        List<Training> entities = trainingRepository.findTraineeTrainingsByOptionalParams(traineeUsername,
                request.getFromDate(), request.getToDate(), request.getTrainerUsername(), request.getTrainingTypeName());

        return trainingMapper.toDtoList(entities);
    }

    @Override
    public List<TrainingResponse> getTrainerTrainings(TrainerTrainingsRequest request) {
        String trainerUsername = request.getTrainerUsername();
        if (!trainerRepository.existsByUserUsername(trainerUsername)) throw new TrainerException(trainerUsername);
        List<Training> entities = trainingRepository.findTrainerTrainingsByOptionalParams(trainerUsername,
                request.getFromDate(), request.getToDate(), request.getTraineeUsername());

        return trainingMapper.toDtoList(entities);
    }
}