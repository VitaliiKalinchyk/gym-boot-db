package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TrainingMapper;
import epam.task.gymbootdb.entity.Trainee;
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

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper trainingMapper;

    @Override
    public void create(TrainingDto request) {
        String username = request.getTrainee().getUser().getUsername();
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeException(username));
        checkIfEntitiesExist(request.getTrainer().getId(),
                             request.getTrainingType().getId());
        Training entity = trainingMapper.toEntity(request);
        entity.setTrainee(trainee);

        trainingRepository.save(entity);
    }

    @Override
    public List<TrainingDto> getTraineeTrainings(TraineeTrainingsRequest request) {
        String username = request.getUsername();
        if (!traineeRepository.existsByUserUsername(username)) throw new TraineeException(username);

        List<Training> entities = trainingRepository.findTraineeTrainingsByOptionalParams(username,
                request.getFromDate(), request.getToDate(), request.getTrainerId(), request.getTrainingTypeId());

        return trainingMapper.toDtoList(entities);
    }

    @Override
    public List<TrainingDto> getTrainerTrainings(TrainerTrainingsRequest request) {
        String username = request.getUsername();
        if (!trainerRepository.existsByUserUsername(username)) throw new TrainerException(username);

        List<Training> entities = trainingRepository.findTrainerTrainingsByOptionalParams(username,
                request.getFromDate(), request.getToDate(), request.getTraineeId());

        return trainingMapper.toDtoList(entities);
    }

    private void checkIfEntitiesExist(long trainerId, long trainingTypeId) {
        if (!trainerRepository.existsById(trainerId)) throw new TrainerException(trainerId);
        if (!trainingTypeRepository.existsById(trainingTypeId)) throw new TrainingTypeException(trainingTypeId);
    }
}
