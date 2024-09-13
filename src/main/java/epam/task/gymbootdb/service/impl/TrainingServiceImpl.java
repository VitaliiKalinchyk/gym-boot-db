package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingCreateRequest;
import epam.task.gymbootdb.dto.TrainingResponse;
import epam.task.gymbootdb.dto.mapper.TrainingMapper;
import epam.task.gymbootdb.entity.Training;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingRepository;
import epam.task.gymbootdb.service.TrainingService;

import static epam.task.gymbootdb.exception.GymExceptions.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingMapper trainingMapper;

        @Override
        @Transactional
        public TrainingResponse create(TrainingCreateRequest request) {
            return trainingMapper.toDto(trainingRepository.save(trainingMapper.toEntity(request)));
        }

    @Override
    public TrainingResponse getById(long id) {
        Training entity = trainingRepository.findById(id).orElseThrow(() -> noSuchTraining(id));

        return trainingMapper.toDto(entity);
    }

    @Override
    public List<TrainingResponse> getAll() {
        return trainingMapper.toDtoList(trainingRepository.findAll());
    }

    @Override
    public List<TrainingResponse> getTraineeTrainings(TraineeTrainingsRequest request) {
        String traineeUsername = request.getTraineeUsername();
        if (!traineeRepository.existsByUserUsername(traineeUsername)) throw noSuchTrainee(traineeUsername);
        List<Training> entities = trainingRepository.findTraineeTrainingsByOptionalParams(traineeUsername,
                request.getFromDate(), request.getToDate(), request.getTrainerUsername(), request.getTrainingTypeName());

        return trainingMapper.toDtoList(entities);
    }

    @Override
    public List<TrainingResponse> getTrainerTrainings(TrainerTrainingsRequest request) {
        String trainerUsername = request.getTrainerUsername();
        if (!trainerRepository.existsByUserUsername(trainerUsername)) throw noSuchTrainer(trainerUsername);
        List<Training> entities = trainingRepository.findTrainerTrainingsByOptionalParams(trainerUsername,
                request.getFromDate(), request.getToDate(), request.getTraineeUsername());

        return trainingMapper.toDtoList(entities);
    }
}