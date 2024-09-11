package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.entity.Training;
import epam.task.gymbootdb.repository.TrainingRepository;
import epam.task.gymbootdb.service.TrainingService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;

    @Override
    public Training add(Training training) {
        return trainingRepository.save(training);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> getById(long id) {
        return trainingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> getByName(String name) {
        return trainingRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings() {
        return trainingRepository.findAll();
    }
}