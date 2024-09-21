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
import epam.task.gymbootdb.service.TrainingService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingService {

    public static final String TRANSACTION_ID = "transactionId";

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingMapper trainingMapper;

    @Override
    public void create(TrainingDto request) {
        Training entity = trainingMapper.toEntity(request);

        trainingRepository.save(entity);
        log.debug("Training (id = {}) was created. Service layer. TransactionId: {}",
                entity.getId(), MDC.get(TRANSACTION_ID));
    }

    @Override
    public List<TrainingDto> getTraineeTrainings(TraineeTrainingsRequest request) {
        long traineeId = request.getTraineeId();
        if (!traineeRepository.existsById(traineeId)) throw new TraineeException(traineeId);
        List<Training> entities = trainingRepository.findTraineeTrainingsByOptionalParams(traineeId,
                request.getFromDate(), request.getToDate(), request.getTrainerId(), request.getTrainingTypeId());
        log.debug("Trainee (id = {}) got his trainings. Service layer. TransactionId: {}",
                request.getTraineeId(), MDC.get(TRANSACTION_ID));

        return trainingMapper.toDtoList(entities);
    }

    @Override
    public List<TrainingDto> getTrainerTrainings(TrainerTrainingsRequest request) {
        long trainerId = request.getTrainerId();
        if (!trainerRepository.existsById(trainerId)) throw new TrainerException(trainerId);
        List<Training> entities = trainingRepository.findTrainerTrainingsByOptionalParams(trainerId,
                request.getFromDate(), request.getToDate(), request.getTraineeId());
        log.debug("Trainer (id = {}) got his trainings. Service layer. TransactionId: {}",
                request.getTraineeId(), MDC.get(TRANSACTION_ID));

        return trainingMapper.toDtoList(entities);
    }
}