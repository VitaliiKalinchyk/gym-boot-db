package epam.task.gymbootdb.service;

import epam.task.gymbootdb.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {

    Training create(Training training);

    Optional<Training> getById(long id);

    Optional<Training> getByName(String name);

    List<Training> getAll();

    List<Training> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                       String trainerUsername, String trainingTypeName);

    List<Training> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                       String traineeUsername);
}