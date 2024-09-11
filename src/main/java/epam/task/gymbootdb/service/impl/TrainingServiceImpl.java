package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.entity.Training;
import epam.task.gymbootdb.repository.TrainingRepository;
import epam.task.gymbootdb.service.TrainingService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public Training create(Training training) {
        return trainingRepository.save(training);
    }

    @Override
    public Optional<Training> getById(long id) {
        return trainingRepository.findById(id);
    }

    @Override
    public Optional<Training> getByName(String name) {
        return trainingRepository.findByName(name);
    }

    @Override
    public List<Training> getAll() {
        return trainingRepository.findAll();
    }

    @Override
    public List<Training> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                              String trainerUsername, String trainingTypeName) {
        return trainingRepository.findTrainingsByOptionalParams(traineeUsername, fromDate, toDate,
                                                                trainerUsername, trainingTypeName);
    }

    @Override
    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                              String traineeUsername) {
        return trainingRepository.findTrainingsByOptionalParams(trainerUsername, fromDate, toDate, traineeUsername);
    }
}