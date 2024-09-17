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

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingMapper trainingMapper;

    @Override
    public void create(TrainingCreateRequest request) {
        Training entity = trainingMapper.toEntity(request);
        trainingRepository.save(entity);
    }

    @Override
    public List<TrainingResponse> getTraineeTrainings(TraineeTrainingsRequest request) {
        long traineeId = request.getTraineeId();
        if (!traineeRepository.existsById(traineeId)) throw new TraineeException(traineeId);
        List<Training> entities = trainingRepository.findTraineeTrainingsByOptionalParams(traineeId,
                request.getFromDate(), request.getToDate(), request.getTrainerId(), request.getTrainingTypeId());

        return trainingMapper.toDtoList(entities);
    }

    @Override
    public List<TrainingResponse> getTrainerTrainings(TrainerTrainingsRequest request) {
        long trainerId = request.getTrainerId();
        if (!trainerRepository.existsById(trainerId)) throw new TrainerException(trainerId);
        List<Training> entities = trainingRepository.findTrainerTrainingsByOptionalParams(trainerId,
                request.getFromDate(), request.getToDate(), request.getTraineeId());

        return trainingMapper.toDtoList(entities);
    }
}