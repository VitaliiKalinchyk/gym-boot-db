package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingCreateRequest;
import epam.task.gymbootdb.dto.TrainingResponse;

import java.util.List;

public interface TrainingService {

    TrainingResponse create(TrainingCreateRequest training);

    TrainingResponse getById(long id);

    List<TrainingResponse> getAll();

    List<TrainingResponse> getTraineeTrainings(TraineeTrainingsRequest request);

    List<TrainingResponse> getTrainerTrainings(TrainerTrainingsRequest request);
}