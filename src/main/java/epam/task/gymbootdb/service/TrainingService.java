package epam.task.gymbootdb.service;

import epam.task.gymbootdb.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingService {

    Training add(Training training);

    Optional<Training> getById(long id);

    Optional<Training> getByName(String name);

    List<Training> getTrainings();
}