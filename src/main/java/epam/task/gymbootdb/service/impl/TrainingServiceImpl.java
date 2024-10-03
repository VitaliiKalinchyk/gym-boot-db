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

    private static final String TRANSACTION_ID = "transactionId";

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
        String username = request.getUsername();
        checkIfTraineeExists(username);

        List<Training> entities = trainingRepository.findTraineeTrainingsByOptionalParams(username,
                request.getFromDate(), request.getToDate(), request.getTrainerId(), request.getTrainingTypeId());
        log.debug("Trainee (username = {}) got his trainings. Service layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return trainingMapper.toDtoList(entities);
    }

    @Override
    public List<TrainingDto> getTrainerTrainings(TrainerTrainingsRequest request) {
        String username = request.getUsername();
        checkIfTrainerExists(username);

        List<Training> entities = trainingRepository.findTrainerTrainingsByOptionalParams(username,
                request.getFromDate(), request.getToDate(), request.getTraineeId());
        log.debug("Trainer (username = {}) got his trainings. Service layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return trainingMapper.toDtoList(entities);
    }

    private void checkIfEntitiesExist(long traineeId, long trainerId, long trainingTypeId) {
        if (!traineeRepository.existsById(traineeId)) throw new TraineeException(traineeId);
        if (!trainerRepository.existsById(trainerId)) throw new TrainerException(trainerId);
        if (!trainingTypeRepository.existsById(trainingTypeId)) throw new TrainingTypeException(trainingTypeId);
    }

    private void checkIfTraineeExists(String username) {
        if (!traineeRepository.existsByUserUsername(username)) throw new TraineeException(username);
    }

    private void checkIfTrainerExists(String username) {
        if (!trainerRepository.existsByUserUsername(username)) throw new TrainerException(username);
    }
}
