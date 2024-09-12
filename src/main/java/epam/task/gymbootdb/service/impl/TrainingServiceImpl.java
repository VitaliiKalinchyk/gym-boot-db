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

import jakarta.persistence.EntityNotFoundException;
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
        //TODO custom exceptions
        Training entity = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training with id = " + id + " not found"));

        return trainingMapper.toDto(entity);
    }

    @Override
    public TrainingResponse getByName(String name) {
        //TODO custom exceptions
        Training entity = trainingRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Training with name = " + name + " not found"));

        return trainingMapper.toDto(entity);
    }

    @Override
    public List<TrainingResponse> getAll() {
        return trainingMapper.toDtoList(trainingRepository.findAll());
    }

    @Override
    public List<TrainingResponse> getTraineeTrainings(TraineeTrainingsRequest request) {
        boolean traineeExists = traineeRepository.existsByUserUsername(request.getTraineeUsername());
        if (!traineeExists) {
            throw new EntityNotFoundException("Trainee not found with username: " + request.getTraineeUsername());
        }
        //TODO custom exceptions

        List<Training> entities = trainingRepository.findTraineeTrainingsByOptionalParams(request.getTraineeUsername(),
                request.getFromDate(), request.getToDate(), request.getTrainerUsername(), request.getTrainingTypeName());

        return trainingMapper.toDtoList(entities);
    }

    @Override
    public List<TrainingResponse> getTrainerTrainings(TrainerTrainingsRequest request) {
        boolean trainerExists = trainerRepository.existsByUserUsername(request.getTrainerUsername());
        if (!trainerExists) {
            throw new EntityNotFoundException("Trainer not found with username: " + request.getTrainerUsername());
        }
        //TODO custom exceptions

        List<Training> entities = trainingRepository.findTrainerTrainingsByOptionalParams(request.getTrainerUsername(),
                request.getFromDate(), request.getToDate(), request.getTraineeUsername());

        return trainingMapper.toDtoList(entities);
    }
}