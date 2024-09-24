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
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper trainingMapper;

    @Override
    public void create(TrainingDto request) {
        checkIfEntitiesExist(request.getTrainee().getId(), request.getTrainer().getId(),
                             request.getTrainingType().getId());
        Training entity = trainingMapper.toEntity(request);

        trainingRepository.save(entity);
        log.debug("Training (id = {}) was created. Service layer. TransactionId: {}",
                entity.getId(), MDC.get(TRANSACTION_ID));
    }

    @Override
    public List<TrainingDto> getTraineeTrainings(TraineeTrainingsRequest request) {
        long traineeId = request.getTraineeId();
        checkIfTraineeExists(traineeId);
        List<Training> entities = trainingRepository.findTraineeTrainingsByOptionalParams(traineeId,
                request.getFromDate(), request.getToDate(), request.getTrainerId(), request.getTrainingTypeId());
        log.debug("Trainee (id = {}) got his trainings. Service layer. TransactionId: {}",
                request.getTraineeId(), MDC.get(TRANSACTION_ID));

        return trainingMapper.toDtoList(entities);
    }

    @Override
    public List<TrainingDto> getTrainerTrainings(TrainerTrainingsRequest request) {
        long trainerId = request.getTrainerId();
        checkIfTrainerExists(trainerId);
        List<Training> entities = trainingRepository.findTrainerTrainingsByOptionalParams(trainerId,
                request.getFromDate(), request.getToDate(), request.getTraineeId());
        log.debug("Trainer (id = {}) got his trainings. Service layer. TransactionId: {}",
                request.getTraineeId(), MDC.get(TRANSACTION_ID));

        return trainingMapper.toDtoList(entities);
    }

    private void checkIfEntitiesExist(long traineeId, long trainerId, long trainingTypeId) {
        checkIfTraineeExists(traineeId);
        checkIfTrainerExists(trainerId);
        checkIfTrainingTypeExists(trainingTypeId);
    }

    private void checkIfTraineeExists(long id) {
        if (!traineeRepository.existsById(id)) throw new TraineeException(id);
    }

    private void checkIfTrainerExists(long id) {
        if (!trainerRepository.existsById(id)) throw new TrainerException(id);
    }

    private void checkIfTrainingTypeExists(long id) {
        if (!trainingTypeRepository.existsById(id)) throw new TrainingTypeException(id);
    }
}
